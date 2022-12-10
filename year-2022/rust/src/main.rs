use std::{fs, ops::Range, collections::{BTreeMap, HashSet}};

fn main() {
    //day_1();
    //day_2();
    //day_3();
    //day_4();
    //day_5();
    //day_6();
    day_7();
}

#[allow(dead_code)]
#[allow(unused_variables)]
fn day_7() {
    let input = read_input("../7/input");

    struct DirectoryEntry<'a> {
        name: String,
        directories: &'a mut Vec<&'a mut DirectoryEntry<'a>>,
        files: Vec<FileEntry>
    }

    impl<'a> DirectoryEntry<'a> {
        fn add_directory(&mut self, dir: &'a mut DirectoryEntry<'a>) {
            self.directories.push(dir);
        }

        fn find_directory(&self, name: &str) -> Option<&'a mut DirectoryEntry<'a>> {
            for directory in self.directories.iter() {
                if &directory.name == &String::from(name) {
                    // TODO: fix
                    //return Some(directory);
                }
            }

            None
        }
    }

    struct FileEntry {
        name: String,
        size: u32
    }

    let mut root = DirectoryEntry {
        name: String::from("/"),
        directories: &mut Vec::new(),
        files: Vec::new()
    };

    let mut lines = input.lines();
    let first_line = lines.next().unwrap();

    assert!(first_line == "$ cd /");
    let current_dir: &mut DirectoryEntry = &mut root;

    for line in lines {
        if line.starts_with("$ ") {
            if line == "$ ls" {
                continue;
            }
            else if line == "$ cd .." {
                // TODO: handle
            }
            else if line.starts_with("$ cd ") {
                let (_, target_directory_name) = line.split_once("$ cd ")
                    .expect("Expected a directory name");

                /*
                current_dir = current_dir.find_directory(target_directory_name)
                    .expect("A directory to be found");
                */

                /*
                let mut found_directory = false;
                for mut directory in current_dir.directories.iter() {
                    if directory.name == String::from(target_directory_name) {
                        current_dir = &mut directory;
                        found_directory = true;
                        break;
                    }
                }

                assert!(found_directory, "Expected directory '{}' to exist", target_directory_name);
                */
            }
        }
        else if line.starts_with("dir ") {
            let (_, dir_name) = line.split_once(" ")
                .expect("Expected directory name");

            //println!("Adding dir {}", &dir_name);

            /*
            let new_directory_directories: &mut Vec<&mut DirectoryEntry> = &mut Vec::new();
            let new_directory_files: Vec<FileEntry> = Vec::new();

            let new_directory = DirectoryEntry {
                name: String::from(dir_name),
                directories: new_directory_directories,
                files: new_directory_files
            };

            current_dir.add_directory(&mut new_directory);
            */
        }
        else {
            let file_parts = line.split_once(" ")
                .expect("Expected file size and name");

            let file_size: u32 = file_parts.0.parse()
                .expect("Expected an integer");

            let file_name = file_parts.1;

            //println!("Adding file {}", &file_name);

            let new_file = FileEntry {
                name: String::from(file_name),
                size: file_size
            };

            current_dir.files.push(new_file);
        }
    }
}

#[allow(dead_code)]
fn day_6() {
    let original_input = read_input("../6/input");
    let input_part_1 = original_input.clone();
    let input_part_2 = original_input.clone();

    assert!(input_part_1.len() > 4);
    assert!(input_part_2.len() > 14);

    let mut i = 0;
    let mut current_needle;

    while i + 4 < input_part_1.len() {
        current_needle = input_part_1[i..i+4].to_string();
        let mut seen_chars: HashSet<char> = HashSet::new();
        for c in current_needle.chars() {
            seen_chars.insert(c);
        }

        if seen_chars.len() == 4 {
            println!("Part 1: {}", i + 4);
            break;
        }

        i += 1;
    }

    let mut j = 0;
    let mut current_needle;

    while j + 14 < input_part_1.len() {
        current_needle = input_part_1[j..(j + 14)].to_string();
        let mut seen_chars: HashSet<char> = HashSet::new();
        for c in current_needle.chars() {
            seen_chars.insert(c);
        }

        if seen_chars.len() == 14 {
            println!("Part 2: {}", j + 14);
            break;
        }

        j += 1;
    }

}

#[allow(dead_code)]
fn day_5() {
    let input = read_input("../5/input");
    let lines = input.split("\n");

    let mut input_stacks: Vec<String> = Vec::new();
    let mut input_commands: Vec<String> = Vec::new();
    let mut original_stacks: BTreeMap<i32, Vec<String>> = BTreeMap::new();

    let mut all_stacks_read = false;

    for line in lines {
        if line.is_empty() && !all_stacks_read {
            all_stacks_read = true;
        }
        else if !all_stacks_read {
            input_stacks.push(line.to_string());
        }
        else {
            input_commands.push(line.to_string());
        }
    }

    if input_stacks.is_empty() || input_commands.is_empty() {
        panic!("Empty input stacks or commands");
    }

    let input_line_with_stacks = input_stacks.pop().unwrap();

    for s in input_line_with_stacks.split_whitespace() {
        original_stacks.insert(
            s.parse().expect("Expected integer"),
            Vec::new()
        );
    }

    for line_with_stack_content in input_stacks.iter().rev() {
        let mut i = 0;

        loop {
            let mut j = i + 4;
            let mut last_entry = false;

            while j >= line_with_stack_content.len() {
                j -= 1;
                last_entry = true;
            }

            let stack_entry = line_with_stack_content[i..j].trim().to_string();

            if !stack_entry.is_empty() {
                let stack_id: i32 = ((i / 4) + 1).try_into().unwrap();

                let stack_for_id: &mut Vec<String> = original_stacks.get_mut(&stack_id)
                    .expect("a stack");

                stack_for_id.push(stack_entry[1..2].to_string());
            }

            if last_entry || j >= line_with_stack_content.len() - 1 || i >= j {
                break;
            }

            i += 4;
        }
    }

    let mut stacks_part_1: BTreeMap<i32, Vec<String>> = original_stacks.clone();

    for command in &input_commands {
        let command_elements: Vec<&str> = command
            .split_whitespace()
            .collect();

        assert!(command_elements.len() == 6);

        let number_of_moves: i32 = command_elements.get(1).unwrap().parse::<i32>()
            .expect("Expected an integer");

        let from: i32 = command_elements.get(3).unwrap().parse().expect("Expected an integer");
        let to: i32 = command_elements.get(5).unwrap().parse().expect("Expected an integer");

        for _ in 0..number_of_moves {
            let from_stack = stacks_part_1.get_mut(&from).unwrap();
            let element = from_stack.pop().unwrap();

            let to_stack = stacks_part_1.get_mut(&to).unwrap();
            to_stack.push(element);
        }
    }

    println!("\nPart 1 result: ");

    for (_, stack) in &stacks_part_1 {
        print!("{}", stack.last().expect("Expected at least one element"));
    }
    println!("");

    let mut stacks_part_2: BTreeMap<i32, Vec<String>> = original_stacks.clone();

    for command in &input_commands {
        let command_elements: Vec<&str> = command
            .split_whitespace()
            .collect();

        assert!(command_elements.len() == 6);

        let number_of_moves: i32 = command_elements.get(1).unwrap().parse::<i32>()
            .expect("Expected an integer");

        let from: i32 = command_elements.get(3).unwrap().parse().expect("Expected an integer");
        let to: i32 = command_elements.get(5).unwrap().parse().expect("Expected an integer");

        let from_stack = stacks_part_2.get_mut(&from).unwrap();
        let mut temp_stack: Vec<String> = Vec::new();

        for _ in 0..number_of_moves {
            temp_stack.push(from_stack.pop().unwrap());
        }

        let to_stack = stacks_part_2.get_mut(&to).unwrap();

        while !temp_stack.is_empty() {
            to_stack.push(temp_stack.pop().expect("Expected an element"));
        }
    }

    println!("\nPart 2 result: ");

    for (_, stack) in &stacks_part_2 {
        print!("{}", stack.last().expect("Expected at least one element"));
    }
    println!("");
}

#[allow(dead_code)]
fn day_4() {
    let input = read_input("../4/input");
    let lines = input.split("\n");

    struct RawPairAssignment {
        pair1: String,
        pair2: String
    }

    let mut raw_pair_assignments: Vec<RawPairAssignment> = Vec::new();

    for line in lines {
        let parts = line.split_once(",");
        let (pair1_assignment, pair2_assignment) = parts.unwrap();

        let pair_assignment = RawPairAssignment {
            pair1: pair1_assignment.to_string(),
            pair2: pair2_assignment.to_string()
        };

        raw_pair_assignments.push(pair_assignment);
    }

    struct Assignment {
        lower_bound: i32,
        upper_bound: i32,
        range: Range<i32>
    }

    struct PairAssignment {
        pair1: Assignment,
        pair2: Assignment
    }

    let mut count_of_pairs_with_fully_overlapped_assignments = 0;
    let mut count_of_pairs_with_partially_overlapped_assignments = 0;

    for raw_pair_assignment in &raw_pair_assignments {
        let pair1_range = split_range_input(&raw_pair_assignment.pair1);
        let pair2_range = split_range_input(&raw_pair_assignment.pair2);

        let pair1_assignment = Assignment {
            lower_bound: pair1_range.0,
            upper_bound: pair1_range.1,
            range: pair1_range.0..pair1_range.1
        };

        let pair2_assignment = Assignment {
            lower_bound: pair2_range.0,
            upper_bound: pair2_range.1,
            range: pair2_range.0..pair2_range.1
        };

        if &pair1_assignment.lower_bound >= &pair2_assignment.lower_bound &&
                &pair1_assignment.upper_bound <= &pair2_assignment.upper_bound {

            count_of_pairs_with_fully_overlapped_assignments += 1;
            count_of_pairs_with_partially_overlapped_assignments += 1;
        }
        else if &pair2_assignment.lower_bound >= &pair1_assignment.lower_bound &&
                &pair2_assignment.upper_bound <= &pair1_assignment.upper_bound {

            count_of_pairs_with_fully_overlapped_assignments += 1;
            count_of_pairs_with_partially_overlapped_assignments += 1;
        }
        else if (
                    &pair1_assignment.lower_bound <= &pair2_assignment.upper_bound &&
                    &pair1_assignment.lower_bound >= &pair2_assignment.lower_bound
                ) || (
                    &pair2_assignment.lower_bound <= &pair1_assignment.upper_bound &&
                    &pair2_assignment.lower_bound >= &pair1_assignment.lower_bound
                ) || (
                    &pair1_assignment.upper_bound >= &pair2_assignment.lower_bound &&
                    &pair1_assignment.upper_bound <= &pair2_assignment.upper_bound
                ) || (
                    &pair2_assignment.upper_bound >= &pair1_assignment.lower_bound &&
                    &pair2_assignment.upper_bound <= &pair1_assignment.upper_bound
                ) {

            count_of_pairs_with_partially_overlapped_assignments += 1;
        }
    }

    println!("Pairs with fully overlapping assignments: {}", count_of_pairs_with_fully_overlapped_assignments);
    println!("Pairs with partially overlapping assignments: {}", count_of_pairs_with_partially_overlapped_assignments);

    fn split_range_input(input: &String) -> (i32, i32) {
        let split_input = input.split_once("-");

        let lower_bound: i32 = split_input.unwrap().0.parse()
            .expect("Expected an integer");

        let upper_bound: i32 = split_input.unwrap().1.parse()
            .expect("Expected an integer");

        (lower_bound, upper_bound)
    }
}

#[allow(dead_code)]
fn day_3() {
    let input = read_input("../3/input");
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
    let input = read_input("../2/input");
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
        match choice {
            Choice::Rock => 1,
            Choice::Paper => 2,
            Choice::Scissors => 3
        }
    }

    fn is_first_winner(choice1: &Choice, choice2: &Choice) -> bool {
        match choice1 {
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
        }
    }
}


#[allow(dead_code)]
fn day_1() {
    let input = read_input("../1/input");

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
    fs::read_to_string(&path)
        .expect("Should have been able to read the file")
}
