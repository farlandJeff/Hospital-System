import socket
import zipfile
import json
import tqdm
import os
import subprocess
import sys
import time
from threading import Thread

# setup and general handling of connections
def setup_server():
    global s
    global SERVER_HOST
    global SERVER_PORT
    global BUFFER_SIZE
    global SEPARATOR

    # config
    SERVER_HOST = "0.0.0.0"
    SERVER_PORT = 5001
    BUFFER_SIZE = 4096
    SEPARATOR = "[-]"

    # create and bind
    try:
        s = socket.socket()
        s.bind((SERVER_HOST, SERVER_PORT))
    except socket.error as e:
        print(f"[X] Error creating and binding socket: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[...] Socket created and bound successfully")

    s.listen(5)
    print(f"[*] Listening as {SERVER_HOST}:{SERVER_PORT}")

def handle_connection():
    # accept connection
    try:
        client_socket, address = s.accept()
    except socket.error as e:
        print(f"[X] Error establishing connection: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    except KeyboardInterrupt:
        print(f"[---] Server safely closed")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[+] {address} is connected.")

    return (client_socket, address)

def process_request(client_socket, address):
    # receive request from client
    try:
        received = client_socket.recv(BUFFER_SIZE).decode()
    except socket.error as e:
        print(f"[X] Error receiving request: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[<] Received {received} from {address}")
        request, data = received.split('!')

    # execute function based on request
    switcher = {
        "REQ_FILE_UPLOAD": receive_file,
        "REQ_FILE_DOWNLOAD": send_file,
        "REQ_FILE_LIST": send_files_info,
        "REQ_FILE_DELETE": delete_file,
        "REQ_PATIENT_ADD": add_patient,
        "REQ_PATIENT_EDIT": edit_patient,
        "REQ_PATIENT_LIST": send_patients_info,
        "REQ_PATIENT_DELETE": delete_patient,
        }

    args = {
        "REQ_FILE_UPLOAD": [client_socket, address, data],
        "REQ_FILE_DOWNLOAD": [client_socket, address, data],
        "REQ_FILE_LIST": [client_socket, address, data],
        "REQ_FILE_DELETE": [address, data],
        "REQ_PATIENT_ADD": [address, data],
        "REQ_PATIENT_EDIT": [address, data],
        "REQ_PATIENT_LIST": [client_socket, address],
        "REQ_PATIENT_DELETE": [address, data]
        }

    message = switcher[request](args[request])
    client_socket.close()
    print(message)

# corresponding functions for each client script
def receive_file(args):
    client_socket = args[0]
    address = args[1]
    data = args[2]

    # receive and transform the file infos
    patient_name, filename, filesize = data.split(SEPARATOR)
    filename = os.path.basename(filename)
    filesize = int(filesize)

    # receive file
    progress = tqdm.tqdm(range(filesize), f"[<] Receiving {filename}", unit="B", unit_scale=True, unit_divisor=1024)
    with open("./temp/client.zip", "wb") as f:
        try:
            while True:
                bytes_read = client_socket.recv(BUFFER_SIZE)
                if not bytes_read:
                                break
                f.write(bytes_read)
                progress.update(len(bytes_read))
        except socket.error as e:
            print(f"[X] Error receiving file: {e}")
            s.shutdown(socket.SHUT_RDWR)
            s.close()
            sys.exit(1)
        except IOError as e:
            print(f"[X] Error writing file: {e}")
            s.shutdown(socket.SHUT_RDWR)
            s.close()
            sys.exit(1)
        else:
            print(f"[<] {filename} received from {address}")

    unzip_file("./temp/client.zip")
    put_to_irods(filename, patient_name)

    return f"[O] REQ_FILE_UPLOAD by {address} fulfilled"

def add_patient(args):
    address = args[0]
    data = args[1]

    try:
        patient_data = json.loads(data)
    except ValueError as e:
        print(f"Error loading json: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)

    # check if patient exists
    dir_path = f"/tempZone/home/public/{patient_data['last_name'].upper()}_{patient_data['first_name'].upper()}"
    if dir_exists(dir_path):
        return f"[X] Could not fulfill REQ_PATIENT_ADD by {address}: patient already exists"

    # create new patient dir and insert metadata
    run_cmd(f"imkdir {dir_path}")
    run_cmd(f"imeta add -C {dir_path} first_name {patient_data['first_name']}")
    run_cmd(f"imeta add -C {dir_path} middle_name {patient_data['middle_name']}")
    run_cmd(f"imeta add -C {dir_path} last_name {patient_data['last_name']}")
    run_cmd(f"imeta add -C {dir_path} date_created {patient_data['date_created']}")
    run_cmd(f"imeta add -C {dir_path} date_modified {patient_data['date_modified']}")
    run_cmd(f"imeta add -C {dir_path} height {patient_data['height']}")
    run_cmd(f"imeta add -C {dir_path} weight {patient_data['weight']}")
    run_cmd(f"imeta add -C {dir_path} dob {patient_data['dob']}")
    run_cmd(f"imeta add -C {dir_path} sex {patient_data['sex']}")
    run_cmd(f"imeta add -C {dir_path} ethnicity {patient_data['ethnicity']}")

    return f"[O] REQ_PATIENT_ADD by {address} fulfilled"

def edit_patient(args):
    address = args[0]
    data = args[1]

    try:
        patient_data = json.loads(data)
    except ValueError as e:
        print(f"[X] Error loading json: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)

    # set dir metadata to new values
    dir_path = f"/tempZone/home/public/{patient_data['last_name'].upper()}_{patient_data['first_name'].upper()}"
    run_cmd(f"imeta set -C {dir_path} first_name {patient_data['first_name']}")
    run_cmd(f"imeta set -C {dir_path} middle_name {patient_data['middle_name']}")
    run_cmd(f"imeta set -C {dir_path} last_name {patient_data['last_name']}")
    run_cmd(f"imeta set -C {dir_path} date_created {patient_data['date_created']}")
    run_cmd(f"imeta set -C {dir_path} date_modified {patient_data['date_modified']}")
    run_cmd(f"imeta set -C {dir_path} height {patient_data['height']}")
    run_cmd(f"imeta set -C {dir_path} weight {patient_data['weight']}")
    run_cmd(f"imeta set -C {dir_path} dob {patient_data['dob']}")
    run_cmd(f"imeta set -C {dir_path} sex {patient_data['sex']}")
    run_cmd(f"imeta set -C {dir_path} ethnicity {patient_data['ethnicity']}")

    return f"[O] REQ_PATIENT_EDIT by {address} fulfilled"

def send_patients_info(args):
    client_socket = args[0]
    address = args[1]

    patient_data = []

    # fetch patient dir names
    dir_names = run_cmd("ils /tempZone/home/public | awk -F '/' '/_/ {print $5}'").splitlines()

    # fetch patient dir metadata
    for dir_name in dir_names:
        meta = {}
        result = run_cmd(f"imeta ls -C /tempZone/home/public/{dir_name} | awk '/^[av]/' | cut -f2 -d ' '").splitlines()
        for i in range(0, len(result), 2):
            meta[result[i]] = result[i+1]
        patient_data.append(meta)

    # send to client
    try:
        client_socket.send(json.dumps(patient_data).encode())
    except OSError as e:
        print(f"[X] Error sending patient data: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[>] Patient data sent successfully")

    return f"[O] REQ_FETCH by {address} fulfilled"

def send_files_info(args):
    client_socket = args[0]
    address = args[1]
    data = args[2]

    patient_name, file_age, search_terms = data.split(SEPARATOR)
    patient_dir = '/tempZone/home/public/' + patient_name
    run_cmd('icd ' + patient_dir)      # navigate to patient dir

    # retreive file metadata and parse to json format in a dict
    file_matches = retreive_matching_file_list(patient_dir, file_age, search_terms)
    file_data = parse_file_metadata(patient_dir, file_matches)

    # send to client
    try:
        data_bytes = json.dumps(file_data).encode()
        client_socket.send(data_bytes)
    except OSError as e:
        print(f"[X] Error sending file data: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[>] File data sent successfully | {len(data_bytes)} bytes")

    return f"[O] REQ_FILE_LIST by {address} fulfilled"

def send_file(args):
    client_socket = args[0]
    address = args[1]
    data = args[2]

    patient_name, filename = data.split(SEPARATOR)
    file_path = f"/tempZone/home/public/{patient_name}/{filename}"
    dest_path = f"./temp/{patient_name}-{filename}"
    meta_path = os.path.splitext(dest_path)[0] + '_meta.txt'

    # compile metadata into a txt
    metadata = {}
    result = run_cmd(f"imeta ls -d {file_path} | awk '/^[av]/' | cut -f2 -d ' '").splitlines()
    for i in range(0, len(result), 2):
        metadata[result[i]] = result[i+1]
    with open(meta_path, 'wb') as f:
        f.write(json.dumps(metadata).encode())

    # fetch, zip, and send the file and metadata
    run_cmd(f"iget '{file_path}' '{dest_path}'")
    file_size = zip_file(dest_path, meta_path)
    try:
        with open('./temp/to_client.zip', "rb") as f:
            while True:
                bytes_read = f.read(BUFFER_SIZE)
                if not bytes_read:
                    break
                client_socket.sendall(bytes_read)
    except Exception as e:
        print(f"[X] Sending file failed: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[>] File sent | {file_size} bytes")

    # clear temp
    os.system("rm ./temp/*")

    return f"[O] REQ_FILE_DOWNLOAD by {address} fulfilled"

def delete_patient(args):
    address = args[0]
    patient_name = args[1]

    # delete patient dir and all files within
    run_cmd(f"irm -rf /tempZone/home/public/{patient_name}")

    return f"[O] REQ_PATIENT_DELETE by {address} fulfilled"

def delete_file(args):
    address = args[0]
    data = args[1]

    patient_name, file_name = data.split(SEPARATOR)

    # delete the specified file
    run_cmd(f"irm '/tempZone/home/public/{patient_name}/{file_name}'")

    return f"[O] REQ_FILE_DELETE by {address} fulfilled"

# utility
def dir_exists(dir):
    output = subprocess.run(['ils', dir], stderr=subprocess.PIPE).stderr.decode('utf-8')
    if output.find("does not exist") == -1:
        return True
    else:
        return False

def run_cmd(cmd):
    try:
        output = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE).stdout.decode('utf-8')
    except subprocess.CalledProcessError as e:
        print(f"[X] Error executing cmd: \"{cmd}:\"\n\t{e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        print(f"[ ] Executed cmd: \"{cmd}\"")
        return output

def retreive_matching_file_list(patient_dir, file_age, search_terms):
    # change the target file age based on client message
    now = time.time()
    MONTH_IN_SEC = 2629746
    AGE_MONTH = now - MONTH_IN_SEC
    AGE_6_MONTH = now - MONTH_IN_SEC * 6
    AGE_YEAR = now - MONTH_IN_SEC * 12
    AGE_5_YEAR = now - MONTH_IN_SEC * 60
    if file_age == '4':
        age = AGE_MONTH
    elif file_age == '3':
        age = AGE_6_MONTH
    elif file_age == '2':
        age = AGE_YEAR
    elif file_age == '1':
        age = AGE_5_YEAR
    else:
        age = 0

    # load a list with all patient files
    file_matches = []
    all_files = run_cmd(f"ils {patient_dir} | fgrep . | cut -f3 -d ' '").splitlines()

    # select files that match the target age and search terms (if any)
    for file in all_files:
        date_create = int(run_cmd(f"imeta ls -d '{patient_dir}/{file}' date_created | awk '/value/ {{print $2}}'"))
        title = run_cmd(f"imeta ls -d '{patient_dir}/{file}' title | awk '/value/ {{print $2}}'")
        print(date_create)
        print(title)
        if date_create >= age:
            if search_terms != 'None':
                if title.find(search_terms) != -1:
                    file_matches.append(file)
            else:
                file_matches.append(file)

    return file_matches

def parse_file_metadata(patient_dir, file_matches):
    file_data = []
    for match in file_matches:
        meta = {}
        result = run_cmd(f"imeta ls -d '{patient_dir}/{match}' | awk '/^[av]/' | cut -f2 -d ' '").splitlines()
        for i in range(0, len(result), 2):
            meta[result[i]] = result[i+1]
        file_data.append(meta)
    return file_data

def unzip_file(path):
    try:
        with zipfile.ZipFile(path, 'r') as zip_ref:
            zip_ref.extractall("./temp")
    except Exception as e:
        print(f"Error unzipping file: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    else:
        os.system("rm ./temp/client.zip")

def zip_file(file_path, meta_path):
    with zipfile.ZipFile('./temp/to_client.zip', 'w') as zip:
        zip.write(file_path)
        zip.write(meta_path)
    return os.path.getsize('./temp/to_client.zip')

def put_to_irods(filename, patient_name):
    # read data from file
    try:
        with open("./temp/meta.txt") as f:
            data = json.load(f)
    except IOError as e:
        print(f"[X] Error opening metadata file: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)
    except ValueError as e:
        print(f"[X] Error loading json: {e}")
        s.shutdown(socket.SHUT_RDWR)
        s.close()
        sys.exit(1)

    # find any files matching the name exactly, as well as any names that match the format of a copy
    namelets = filename.split('.')
    output = run_cmd(f"ils /tempZone/home/public/{patient_name} | awk '/^  {namelets[0]}\(?[0-9]*?\)?.{namelets[1]}$/' | cut -d ' ' -f3")
    count = output.count(namelets[0])

	# modify destination file name based on how many copies of that name exist
    if count > 0:
        copy_number = count
        for x in range(1, count-1):
            if output.find(f"{namelets[0]}({x}).{namelets[1]}") == -1:
                copy_number = x
                break
        # build new filename using copy format
        new_filename = f"'{namelets[0]}({copy_number}).{namelets[1]}'"
    else:
        # use original filename
        new_filename = filename

    run_cmd(f"iput ./temp/{filename} /tempZone/home/public/{patient_name}/{new_filename} --metadata=\"date_created;{data['date_created']};;date_modified;{data['date_modified']};;title;{data['title']};;overseeing;{data['overseeing']};;notes;{data['notes']};;file_name;{data['file_name']};;\"")
    os.system("rm ./temp/*")


#########################################################

setup_server()
while True:
    client_socket, address = handle_connection()
    Thread(target=process_request, args=(client_socket, address)).start()
