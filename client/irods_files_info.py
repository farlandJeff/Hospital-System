import argparse
import os
import socket
import transaction
import zipfile

# parse arguments
parser = argparse.ArgumentParser()
parser.add_argument("patient_name",
                    help="the patient files are being fetched for")
parser.add_argument("-s", "--search_terms",
                    help="the search terms to be sent to server")
parser.add_argument("-a", "--age", choices=['0','1','2','3','4'], default='2',
                    help="the maximum age of files to be fetched " +
                    "(0: all, 1: 5 years, 2: 1 year, 3: 6 months, 4: 1 month)")
args = parser.parse_args()

# build outgoing data string
SEPARATOR = "[-]"
data = args.patient_name + SEPARATOR + str(args.age) + SEPARATOR + str(args.search_terms)

# connect and send
trans = transaction.Request()
trans.connect()
trans.send_req("REQ_FILE_LIST", data)

# receive and write
try:
    with open("file_data.json", "wb") as f:
        f.write(trans.recv_response())
except OSError as e:
    print(f"[X] Writing to file failed: {e}")
    s.shutdown(socket.SHUT_RDWR)
    s.close()
    sys.exit(1)
else:
    size = os.path.getsize("file_data.json")
    if size <= 2:
        print(f"[!<] Received empty file")
    else:
        print(f"[<] Received data from server | {size} bytes")
