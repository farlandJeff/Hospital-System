import socket
import sys
import tqdm

class Request:
    def __init__(self):
        self.SERVER_HOST = "54.227.89.39"
        self.SERVER_PORT = 5001
        self.SEPARATOR = "[-]"
        self.BUFFER_SIZE = 4096
        self.SOCKET = socket.socket()

    def connect(self):
        print(f"[...] Connecting to {self.SERVER_HOST}:{self.SERVER_PORT}")
        try:
            self.SOCKET.connect((self.SERVER_HOST, self.SERVER_PORT))
        except OSError as e:
            print(f"[X] Connection failed: {e}")
            self.SOCKET.shutdown(socket.SHUT_RDWR)
            self.SOCKET.close()
            sys.exit(1)
        else:
            print("[+] Connected")

    def send_req(self, request, data):
        try:
            self.SOCKET.send(f"{request}!{data}".encode())
        except OSError as e:
            print(f"[X] Sending request failed: {e}")
            self.SOCKET.shutdown(socket.SHUT_RDWR)
            self.SOCKET.close()
            sys.exit(1)
        else:
            print(f"[>] {request} sent")

    def send_file(self, filename, filesize):
        try:
            progress = tqdm.tqdm(range(filesize), f"Sending {filename}", unit="B", unit_scale=True, unit_divisor=1024)
            with open('client.zip', "rb") as f:
                while True:
                    bytes_read = f.read(self.BUFFER_SIZE)
                    if not bytes_read:
                        break
                    self.SOCKET.sendall(bytes_read)
                    progress.update(len(bytes_read))
        except Exception as e:
            print(f"[X] Sending file failed: {e}")
            self.SOCKET.shutdown(socket.SHUT_RDWR)
            self.SOCKET.close()
            sys.exit(1)
        else:
            print(f"[>] File sent")
        finally:
            self.SOCKET.close()

    def recv_response(self):
        try:
            message = self.SOCKET.recv(self.BUFFER_SIZE)
            while True:
                bytes_read = self.SOCKET.recv(self.BUFFER_SIZE)
                if not bytes_read:
                	break
                message = message + bytes_read
        except OSError as e:
            print(f"[X] Receiving data failed: {e}")
            self.SOCKET.shutdown(socket.SHUT_RDWR)
            self.SOCKET.close()
            sys.exit(1)
        else:
            return message
        finally:
            self.SOCKET.close()

class In:
    def __init__(self):
        self.SERVER_HOST = "54.227.89.39"
        self.SERVER_PORT = 5001
        self.SEPARATOR = "[-]"
        self.BUFFER_SIZE = 4096
        self.SOCKET = socket.socket()
