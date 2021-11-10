import argparse
import os
import socket
import sys
import transaction
import zipfile

def unzip_file(file_zip, file_name):
    try:
        with zipfile.ZipFile(file_zip, 'r') as zip_ref:
            zip_ref.extractall('.')
    except Exception as e:
        print(f"Error unzipping file: {e}")
        sys.exit(1)
    else:
        os.system("rm " + file_zip)

# parse arguments
parser = argparse.ArgumentParser()
parser.add_argument("patient_name",
                    help="the patient whose record is the destination")
parser.add_argument("file_path",
                    help="the path of the file being uploaded")
args = parser.parse_args()

# build outgoing data string
SEPARATOR = "[-]"
file_name = os.path.basename(args.file_path)
data = f"{args.patient_name}{SEPARATOR}{file_name}"

# connect and send request
trans = transaction.Request()
trans.connect()
trans.send_req("REQ_FILE_DOWNLOAD", data)

# receive and write file
try:
    file_zip = os.path.splitext(file_name)[0] + '.zip'
    with open(file_zip, "wb") as f:
        f.write(trans.recv_response())
except OSError as e:
    print(f"[X] Writing to file failed: {e}")
    s.shutdown(socket.SHUT_RDWR)
    s.close()
    sys.exit(1)
else:
    size = os.path.getsize(file_zip)
    print(f"[<] Received data from server | {size} bytes")

unzip_file(file_zip, file_name)
