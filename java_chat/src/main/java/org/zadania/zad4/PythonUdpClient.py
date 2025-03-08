import socket

serverIP = "127.0.0.1"
serverPort = 8999
msg = "żółta gęś"

client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg, 'utf-8'), (serverIP, serverPort))
print("Message sent!")

buff, address = client.recvfrom(1024)
print("Received: ", buff.decode('utf-8'))