# import pandas as pd
# import json
# import sys
#
# def generate_multi_month_excel_report(input_json_file, output_excel_file):
#     with open(input_json_file, 'r') as f:
#         snapshots = json.load(f)
#
#     if not snapshots:
#         print("No data found in JSON file.")
#         return
#
#     data = []
#     for snapshot in snapshots:
#         snapshot_date = pd.to_datetime(snapshot['snapshotDate'])
#         data.append({
#             'Year': snapshot.get('year', 'N/A'),
#             'Month': snapshot.get('month', 'N/A'),
#             'Product': snapshot.get('product', 'N/A'),
#             'Zone': snapshot.get('zone', 'N/A'),
#             'Quantity': snapshot.get('quantity', 'N/A'),
#             'Snapshot Date': snapshot_date.strftime('%Y-%m-%d')
#         })
#
#     df = pd.DataFrame(data)
#     df.to_excel(output_excel_file, index=False)
#     print(f"Excel report generated at {output_excel_file}")
#
# if __name__ == "__main__":
#     input_json_file = sys.argv[1]
#     output_excel_file = sys.argv[2]
#     generate_multi_month_excel_report(input_json_file, output_excel_file)
import json
import sys
from openpyxl import Workbook

def generate_multi_month_excel_report(input_json_file, output_excel_file):
    with open(input_json_file, 'r') as f:
        snapshots = json.load(f)

    if not snapshots:
        print("No data found in JSON file.")
        return

    workbook = Workbook()
    sheet = workbook.active
    sheet.title = "Multi-Month Report"

    # Write the header
    headers = ['Year', 'Month', 'Product', 'Zone', 'Quantity', 'Snapshot Date']
    sheet.append(headers)

    # Write the data
    for snapshot in snapshots:
        snapshot_date = snapshot.get('snapshotDate', 'N/A')
        row = [
            snapshot.get('year', 'N/A'),
            snapshot.get('month', 'N/A'),
            snapshot.get('product', 'N/A'),
            snapshot.get('zone', 'N/A'),
            snapshot.get('quantity', 'N/A'),
            snapshot_date
        ]
        sheet.append(row)

    # Save the workbook
    workbook.save(output_excel_file)
    print(f"Excel report generated at {output_excel_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python generate_multi_month_excel_report.py <input_json_file> <output_excel_file>")
        sys.exit(1)

    input_json_file = sys.argv[1]
    output_excel_file = sys.argv[2]
    generate_multi_month_excel_report(input_json_file, output_excel_file)

