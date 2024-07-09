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
        snapshot_date = pd.to_datetime(snapshot['snapshotDate'])
        data.append({
            'Warehouse': snapshot.get('warehouse', 'N/A'),
            'Product Name': snapshot.get('product', 'N/A'),
            'Zone Name': snapshot.get('zone', 'N/A'),
            'Quantity': snapshot.get('quantity', 'N/A'),
            'Snapshot Date': snapshot_date.strftime('%Y-%m-%d')
        })

    df = pd.DataFrame(data)
    df.to_excel(output_excel_file, index=False)
    print(f"Excel report generated at {output_excel_file}")

if __name__ == "__main__":
    input_json_file = sys.argv[1]
    output_excel_file = sys.argv[2]
    generate_excel_report(input_json_file, output_excel_file)
