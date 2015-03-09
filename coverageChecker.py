import os, csv
analyzer_dir = os.path.abspath("spreadsheet-analyzer")
jacoco_file = os.path.join(analyzer_dir,"target","site","jacoco","jacoco.csv")

with open(jacoco_file, 'rb') as csvfile:
  reader = csv.reader(csvfile, delimiter=',', quotechar='|')
  for row in reader:
    row_val = 0
    try:
      row_val = int(row[5]) #Instructions missed
    except ValueError:
      print(row)
      continue
    if (row_val > 10):
      print(row[1],row[2], "missed too many instructions in testing") #class name
      exit(5)
      
