use std::{fs};

fn main() {
    //day_1();
    day_2();
}

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
