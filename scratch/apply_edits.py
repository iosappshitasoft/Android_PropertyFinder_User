import sys

def replace_lines(file_path, start_line, end_line, replacement_file):
    with open(file_path, 'r') as f:
        lines = f.readlines()
    
    with open(replacement_file, 'r') as f:
        replacement_content = f.readlines()
    
    # lines is 0-indexed, so start_line-1
    new_lines = lines[:start_line-1] + replacement_content + lines[end_line:]
    
    with open(file_path, 'w') as f:
        f.writelines(new_lines)

if __name__ == "__main__":
    replace_lines(sys.argv[1], int(sys.argv[2]), int(sys.argv[3]), sys.argv[4])
