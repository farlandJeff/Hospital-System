import argparse
import os
import socket
import transaction
import zipfile

# parse arguments
parser = argparse.ArgumentParser()
parser.add_argument("patient_name",
                    help="the patient whose record is the destination")
parser.add_argument("filename",
                    help="the name of the file being uploaded")
args = parser.parse_args()

def zip_file(filename):
	with zipfile.ZipFile('client.zip', 'w') as zip:
		zip.write(filename)
		zip.write('meta.txt')
	return os.path.getsize('client.zip')

# build outgoing data string
SEPARATOR = "[-]"
filesize = zip_file(args.filename)
data = f"{args.patient_name}{SEPARATOR}{args.filename}{SEPARATOR}{filesize}"

# connect and send
trans = transaction.Request()
trans.connect()
trans.send_req("REQ_FILE_UPLOAD", data)
trans.send_file(args.filename, filesize)
