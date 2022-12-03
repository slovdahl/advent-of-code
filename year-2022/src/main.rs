use std::{fs};

fn main() {
    //day_1();
    //day_2();
    day_3();
}

fn day_3() {
    let input = read_input("3/input");
    let lines = input.split("\n");

    if lines.to_owned().collect::<Vec<_>>().len() % 3 != 0 {
        panic!("The number of lines need to be divisible by 3");
    }

    struct Rucksack {
        line: String,
        common_item: char
    }

    let mut rucksacks: Vec<Rucksack> = Vec::new();

    for line in lines {
        let line_length = line.len();
        if line_length % 2 != 0 {
            panic!("Unexpected line length: {line_length}");
        }

        let half_length = line_length / 2;

        let comp1 = &line[0..half_length];
        let comp2 = &line[half_length..line.len()];

        let mut common_char: Option<char> = None;
        for c1c in comp1.chars() {
            if comp2.contains(c1c) {
                common_char = Some(c1c);
                break;
            }
        }

        if common_char == None {
            panic!("No common chars found");
        }

        let rucksack = Rucksack {
            line: line.to_string(),
            common_item: common_char.unwrap()
        };

        //println!("comp1: {}, comp2: {}, common: {}", rucksack.compartment_1, rucksack.compartment_2, rucksack.common_item);
        rucksacks.push(rucksack);
    }

    let mut sum_of_priorities_part_1 = 0;
    for rucksack in &rucksacks {
        let priority = match rucksack.common_item {
            c @ 'a'..='z' => (c as u32) - 96, // ASCII a = 97, a-z 1-26
            c @ 'A'..='Z' => (c as u32) - 38, // ASCII A = 65, A-Z 27-52
            _ => panic!("Unexpected character")
        };

        //println!("Priority for {} (ASCII {}):\t{}", rucksack.common_item, rucksack.common_item as u8, priority);

        sum_of_priorities_part_1 += priority;
    }

    println!("Part 1: sum of priorities: {}", sum_of_priorities_part_1);

    let mut sum_of_priorities_part_2 = 0;
    let mut i = 0;

    loop {
        let elf_1 = rucksacks.get(i).unwrap();
        let elf_2 = rucksacks.get(i + 1).unwrap();
        let elf_3 = rucksacks.get(i + 2).unwrap();

        let mut common_char: Option<char> = None;
        for c1c in elf_1.line.chars() {
            if elf_2.line.contains(c1c) && elf_3.line.contains(c1c) {
                common_char = Some(c1c);
                break;
            }
        }

        if common_char == None {
            panic!("No common chars found");
        }

        let priority = match common_char.unwrap() {
            c @ 'a'..='z' => (c as u32) - 96, // ASCII a = 97, a-z 1-26
            c @ 'A'..='Z' => (c as u32) - 38, // ASCII A = 65, A-Z 27-52
            _ => panic!("Unexpected character")
        };

        sum_of_priorities_part_2 += priority;
        i += 3;

        if i >= rucksacks.len() {
            break;
        }
    }

    println!("Part 2: sum of priorities: {}", sum_of_priorities_part_2);
}

#[allow(dead_code)]
fn day_2() {
    let input = read_input("2/input");
    let lines = input.split("\n");

    enum Choice {
        Rock,
        Paper,
        Scissors
    }

    struct Round {
        player1: Choice,
        player2: Choice
    }

    let mut part_1_rounds: Vec<Round> = Vec::new();

    for line in lines.clone() {
        if line.len() == 0 {
            continue;
        }

        let player_1_choice = match line.chars().nth(0).unwrap() {
            'A' => Choice::Rock,
            'B' => Choice::Paper,
            'C' => Choice::Scissors,
            _ => panic!("Unexpected choice for player 1")
        };

        let player_2_choice = match line.chars().nth(2).unwrap() {
            'X' => Choice::Rock,
            'Y' => Choice::Paper,
            'Z' => Choice::Scissors,
            _ => panic!("Unexpected choice for player 2")
        };

        let round = Round {
            player1: player_1_choice,
            player2: player_2_choice
        };

        part_1_rounds.push(round);
    }

    let mut part_1_player_1_score = 0;
    let mut part_1_player_2_score = 0;

    for round in part_1_rounds {
        part_1_player_1_score += choice_to_score(&round.player1);
        part_1_player_2_score += choice_to_score(&round.player2);

        if is_first_winner(&round.player1, &round.player2) {
            part_1_player_1_score += 6;
        }
        else if is_first_winner(&round.player2, &round.player1) {
            part_1_player_2_score += 6;
        }
        else {
            part_1_player_1_score += 3;
            part_1_player_2_score += 3;
        }
    }

    println!("Part 1, Elf score: {part_1_player_1_score}");
    println!("Part 1, Your score: {part_1_player_2_score}");

    let mut part_2_rounds: Vec<Round> = Vec::new();

    for line in lines.clone() {
        if line.len() == 0 {
            continue;
        }

        let player_1_choice = match line.chars().nth(0).unwrap() {
            'A' => Choice::Rock,
            'B' => Choice::Paper,
            'C' => Choice::Scissors,
            _ => panic!("Unexpected choice for player 1")
        };

        let player_2_choice = match line.chars().nth(2).unwrap() {
            // Lose
            'X' => match player_1_choice {
                Choice::Rock => Choice::Scissors,
                Choice::Paper => Choice::Rock,
                Choice::Scissors => Choice::Paper
            },

            // Draw
            'Y' => match player_1_choice {
                Choice::Rock => Choice::Rock,
                Choice::Paper => Choice::Paper,
                Choice::Scissors => Choice::Scissors
            },

            // Win
            'Z' => match player_1_choice {
                Choice::Rock => Choice::Paper,
                Choice::Paper => Choice::Scissors,
                Choice::Scissors => Choice::Rock
            },

            _ => panic!("Unexpected choice for player 2")
        };

        let round = Round {
            player1: player_1_choice,
            player2: player_2_choice
        };

        part_2_rounds.push(round);
    }

    let mut part_2_player_1_score = 0;
    let mut part_2_player_2_score = 0;

    for round in part_2_rounds {
        part_2_player_1_score += choice_to_score(&round.player1);
        part_2_player_2_score += choice_to_score(&round.player2);

        if is_first_winner(&round.player1, &round.player2) {
            part_2_player_1_score += 6;
        }
        else if is_first_winner(&round.player2, &round.player1) {
            part_2_player_2_score += 6;
        }
        else {
            part_2_player_1_score += 3;
            part_2_player_2_score += 3;
        }
    }

    println!("Part 2, Elf score: {part_2_player_1_score}");
    println!("Part 2, Your score: {part_2_player_2_score}");

    fn choice_to_score(choice: &Choice) -> i32 {
        return match choice {
            Choice::Rock => 1,
            Choice::Paper => 2,
            Choice::Scissors => 3
        };
    }

    fn is_first_winner(choice1: &Choice, choice2: &Choice) -> bool {
        return match choice1 {
            Choice::Rock => match choice2 {
                Choice::Scissors => true,
                _ => false
            },
            Choice::Paper => match choice2 {
                Choice::Rock => true,
                _ => false
            },
            Choice::Scissors => match choice2 {
                Choice::Paper => true,
                _ => false
            }
        };
    }
}


#[allow(dead_code)]
fn day_1() {
    let input = read_input("1/input");

    let elves = input.split("\n\n");

    let mut all_sums: Vec<i32> = Vec::new();

    for elf_content in elves {
        let elf = elf_content.split("\n");

        let mut sum = 0;
        for elf_entry in elf {
            let entry: i32 = elf_entry.parse()
                .expect("Expected an integer");

            sum += entry;
        }

        all_sums.push(sum);
    }

    all_sums.sort_by(|a, b| b.cmp(a));
    let sum_of_top_3: &i32 = &all_sums[0..3].iter().sum();

    println!("Top 3 elves:");
    for e in &all_sums[0..3] {
        println!("{e}");
    }

    println!("\nSum of all 3 top elves: {sum_of_top_3}");
}

fn read_input(path: &str) -> String {
    return fs::read_to_string(&path)
        .expect("Should have been able to read the file");
}
