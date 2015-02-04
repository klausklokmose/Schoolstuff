import time
import BaseHTTPServer
from urlparse import urlparse, parse_qs
import socket

HOST_NAME = "0.0.0.0"  # !!!REMEMBER TO CHANGE THIS!!!
PORT_NUMBER = 1337  # Maybe set this to 9000.
f = 0
called = 0
machinePrice = 15.0
    
class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    
    def do_HEAD(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
    
    
    def do_GET(self):
        query_components = parse_qs(urlparse(self.path).query)
        message = str(query_components.get("message"))[2:-2]
        print message
        if message == "TAG":
            print "Connection set up"
            self.sendResponse("#".join(["setup ok", machinePrice.__str__()]))
        elif message == "recipe":
            print "Recipe information: "+str(query_components.get("recipe"))[2:-2]
            self.sendResponse("Recipe ok")
        elif message == "payment":
            print "Payment processing"
            price = str(query_components.get("price"))
            print "Price is "+ price
            if machinePrice <= user.getBalance():
                print "Balance ok: "+str(user.getBalance())
                self.sendResponse("token115")
            else:
                print "Balance not sufficient"
                self.sendResponse("Balance too low")
        elif message == "orderCoffee":
            recipe = str(query_components.get("recipe"))[2:-2]
            token1 = str(query_components.get("token1"))[2:-2]
            token2 = str(query_components.get("token2"))[2:-2]
            if(recipe is not None and token1 is not None and token2 is not None):
                print "verifying token"
                print "paying..."
                user.subtractFromBalance(15)
                self.sendResponse("Executing order")
                print "Executing order..."
                print "DONE"
                
            
        """Respond to a GET request."""
    def sendResponse(self, msg):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        self.wfile.write(msg)

class User:
    token1 = ""
    token2 = ""
    balance = float(100.0)
    loggedIn = bool(0)
    user="Klaus"
    
    def __init__(self):
        pass
    
    def setToken1(self, token):
        token1 = token
    
    def setToken2(self, token):
        token2 = token
        
    def logIn(self):
        self.loggedIn = bool(1)
        print self.user+" logged in"
    
    def getBalance(self):
        return self.balance
    
    def logOut(self):
        self.loggedIn = bool(0)
        
    def subtractFromBalance(self, ammount):
        print "Subtracting from balance "+str(ammount)
        self.balance = self.balance-ammount
        print "Balance is now: "+str(self.balance)
        

if __name__ == '__main__':
    user = User()
    
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
    print time.asctime(), "Server Starts - %s:%s" % (socket.gethostbyname(socket.gethostname()), PORT_NUMBER)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    print time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER)
