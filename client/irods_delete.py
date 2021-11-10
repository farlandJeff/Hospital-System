import argparse
import os
import socket
import sys
import transaction
import zipfile

# parse arguments
parser = argparse.ArgumentParser()
parser.add_argument("-f", "--file", help="to delete a specific file")
parser.add_argument("-p", "--patient", help="to delete a patient")
args = parser.parse_args()

if args.file and not args.patient:
    print('[X] To delete a file, the patient whose file it is must be specified with -p')
    sys.exit(1)

SEPARATOR = '[-]'
trans = transaction.Request()
trans.connect()
if args.file:
    trans.send_req("REQ_FILE_DELETE", args.patient + SEPARATOR + args.file)
else:
    trans.send_req("REQ_PATIENT_DELETE", args.patient)
