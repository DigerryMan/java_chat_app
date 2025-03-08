import socket

serverIP = "127.0.0.1"
serverPort = 8999
msg_bytes = (300).to_bytes(4, byteorder='little')


client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(msg_bytes, (serverIP, serverPort))
print("Message sent!")

buff, address = client.recvfrom(1024)
print("Received: ", int.from_bytes(buff, byteorder='little'))