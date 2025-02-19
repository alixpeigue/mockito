from glob import glob
from collections import defaultdict
import sys
import shutil


def main():
    if "clean" in sys.argv:
        for filename in glob("**/coverage", recursive=True):
            shutil.rmtree(filename)
        print("Cleaned")
        return

    coverage = defaultdict(set)
    nb_lines = {}

    for filename in glob("**/*.cov", recursive=True):
        with open(filename, "r") as f:
            lines = f.readlines()
            method = lines[0][:-1]
            nb_lines[method] = int(lines[1])
            covered = set(int(el) for el in lines[2].split() if el)
            coverage[lines[0][:-1]] |= covered

    for method in coverage.keys():
        not_covered = set(range(nb_lines[method])) - coverage[method]
        print(f">>=========={method}============<<")
        # print("Coverage data for method", method)
        print(
            f"Branches covered : {len(coverage[method])}/{nb_lines[method]} ({len(coverage[method]) / nb_lines[method] * 100}%)"
        )
        if not_covered:
            print(f"Branches not covered : {not_covered}")
        print()


if __name__ == "__main__":
    main()
