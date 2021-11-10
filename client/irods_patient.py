import argparse
import os
import socket
import transaction
import zipfile

# parse arguments
parser = argparse.ArgumentParser()
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("-a", "--add", help="add new patient")
group.add_argument("-e", "--edit", help="edit existing patient")
group.add_argument("-f", "--fetch", action="store_true", help="fetch data on all patients")
args = parser.parse_args()

trans = transaction.Request()
trans.connect()
if args.add:
    trans.send_req("REQ_PATIENT_ADD", args.add)
elif args.edit:
    trans.send_req("REQ_PATIENT_EDIT", args.edit)
elif args.fetch:
    trans.send_req("REQ_PATIENT_LIST", "null")

    # receive and write
    try:
        with open("patient_data.json", "wb") as f:
            f.write(trans.recv_response())
    except OSError as e:
        print(f"[X] Writing to file failed: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        size = os.path.getsize("patient_data.json")
        if size <= 2:
            print(f"[!<] Received empty file")
        else:
            print(f"[<] Received data from server | {size} bytes")
