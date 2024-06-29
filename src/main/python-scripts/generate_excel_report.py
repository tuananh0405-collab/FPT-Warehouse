import pandas as pd
import json
import sys

def generate_excel_report(input_json_file, output_excel_file):
    with open(input_json_file, 'r') as f:
        snapshots = json.load(f)

    if not snapshots:
        print("No data found in JSON file.")
        return

    data = []
    for snapshot in snapshots:
        data.append({
            'Inventory ID': snapshot['id'],
            'Product ID': snapshot['product']['id'],
            'Product Name': snapshot['product']['name'],
            'Quantity': snapshot['quantity'],
            'Snapshot Date': snapshot['snapshotDate'],
            'Zone ID': snapshot['zone']['id'],
            'Zone Name': snapshot['zone']['name']
        })

    df = pd.DataFrame(data)
    df.to_excel(output_excel_file, index=False)
    print(f"Excel report generated at {output_excel_file}")

if __name__ == "__main__":
    input_json_file = sys.argv[1]
    output_excel_file = sys.argv[2]
    generate_excel_report(input_json_file, output_excel_file)
