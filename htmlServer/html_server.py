import time
import BaseHTTPServer


HOST_NAME = '192.168.0.200' # !!!REMEMBER TO CHANGE THIS!!!
PORT_NUMBER = 80 # Maybe set this to 9000.
f=0

class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):
   def do_HEAD(s):
      s.send_response(200)
      s.send_header("Content-type", "text/html")
      s.end_headers()

   def do_GET(s):
      """Respond to a GET request."""
      s.send_response(200)
      s.send_header("Content-type", "text/html")
      s.end_headers()
      s.wfile.write("<html><head><title>Title goes here.</title></head>")
      s.wfile.write("<body><p>This is a test.</p>")
      # If someone went to "http://something.somewhere.net/foo/bar/",
      # then s.path equals "/foo/bar/".
      #s.wfile.write("<p>You accessed path: %s</p>" % s.path)
      f=open("test.htm")
      try:
         for line in f:
            s.wfile.write(line)
      finally:
         f.close()
      s.wfile.write("</body></html>")
  
if __name__ == '__main__':
   server_class = BaseHTTPServer.HTTPServer
   httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
   print time.asctime(), "Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER)
   try:
      httpd.serve_forever()
   except KeyboardInterrupt:
      pass
   httpd.server_close()
   print time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER)  