# Solutions for Advent of Code

Advent of Code - https://adventofcode.com

## Downloading inputs

Log in using the browser, open developer tools, find a request and its response headers, look for
the `Cookie` header and take the token after `session=` and paste it in `.aoc-token`.

## Initiating component for new year

```bash session
export THIS_YEAR=20xx
export PREVIOUS_YEAR=20yy
mkdir -p year-$THIS_YEAR/input
mkdir -p year-$THIS_YEAR/src/main/java/year$THIS_YEAR
cp year-$PREVIOUS_YEAR/build.gradle year-$THIS_YEAR/build.gradle
cp year-$PREVIOUS_YEAR/src/main/java/year$PREVIOUS_YEAR/App.java \
  year-$THIS_YEAR/src/main/java/year$THIS_YEAR/App.java

sed -i "s/$PREVIOUS_YEAR/$THIS_YEAR/g" year-$THIS_YEAR/build.gradle
sed -i "s/$PREVIOUS_YEAR/$THIS_YEAR/g" year-$THIS_YEAR/src/main/java/year$THIS_YEAR/App.java
```

Then manually add `year-$THIS_YEAR` to `settings.gradle`.
