use std::{fs};

fn main() {
    day_1();
}

fn day_1() {
    let path = &"1/input";
    let input = fs::read_to_string(path)
        .expect("Should have been able to read the file");

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