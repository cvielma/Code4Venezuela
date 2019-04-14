from __future__ import division
from BaseHTTPServer import BaseHTTPRequestHandler
import urlparse, json
from BaseHTTPServer import HTTPServer
import argparse
import copy
import json
import pickle
import socket
import time

import sys
import pandas as pd
import os
import re
import random
import time
import binascii
from bisect import bisect_right
from heapq import heappop, heappush

class RequestHandler(BaseHTTPRequestHandler):

    template = '{"Code":"{0}", "message":"{1}"}'
    db = pickle.load(open("../src/main/resources/minhashes.p", "rb"))
    print 'loaded db with '+ str(len(db)) + ' entries'

    def format_message(self, code="0", message=""):
        """

        :param code: "0" or "1". The former means success, the latter means failure.
        :param message: Message to be written to the client.
        :return: json dictionary
        """
        # return '{"Code":"{0}", "message":"{1}"}'.format(code, message)
        return json.dumps({"Code": code, "message": message})
    
    def shingle_text(self, words, verbose=False):
        # Split spaces before shingling
        words = words.split(" ")
        t0 = time.time()

        # 'shinglesInDoc' will hold all of the unique shingle IDs present in the 
        # current document. If a shingle ID occurs multiple times in the document,
        # it will only appear once in the set (this is a property of Python sets).
        shinglesInDoc = set()

        # For each word in the document...
        for index in range(0, len(words) - 2):
            # Construct the shingle text by combining three words together.
            shingle = words[index] + " " + words[index + 1] + " " + words[index + 2]

            # Hash the shingle to a 32-bit integer.
            crc = binascii.crc32(shingle) & 0xffffffff

            # Add the hash value to the list of shingles for the current document. 
            # Note that set objects will only add the value to the set if the set 
            # doesn't already contain it. 
            shinglesInDoc.add(crc)

        # Count the number of shingles across all documents.
        # totalShingles = totalShingles + (len(words) - 2)

        # Report how long shingling took.
        if verbose:
            print '\nShingling took %.2f sec.' % (time.time() - t0)
            # print '\Average shingles per doc: %.2f' % (totalShingles / numDocs)

        return shinglesInDoc

    def Jaccard_order2(self, y1):
    
        for i in range(0,len(y1)-1):
            for j in range(i+1, len(y1)):
                y1_temp = list(copy.deepcopy(y1))
                del y1_temp[j]
                del y1_temp[i]
                if tuple(y1_temp) in self.db:
                    return True
        return False

    def Jaccard_order1(self, y1):
        
        for i in range(0,len(y1)):
            y1_temp = list(copy.deepcopy(y1))
            del y1_temp[i]
            if tuple(y1_temp) in self.db:
                return True
        return False

    def is_near_duplicate(self, y1):

        first_order = self.Jaccard_order1(y1)
        second_order= self.Jaccard_order2(y1)

        return first_order or second_order
    
    def real_time_inference(self, text):
        """
        """
        if(text == ""):
            return self.format_message("1","Empty tweet")
        
        text = text.encode("utf-8")
            
        
        shingles = tuple(self.shingle_text(text))
        response = {}
        if( shingles not in self.db):
            # New? Or very similar to something we already have? Let's see!
            near_duplicate = self.is_near_duplicate(shingles)

            if near_duplicate:
                response = self.format_message("1","Near duplicate")
            else:
                # New tweet!
                self.db[shingles] = text
                response = self.format_message("0","New tweet")
        else:
            response = self.format_message("1","Existent tweet")
        
        return self.wfile.write(response)
            

    def do_GET(self):
        """

        :return: It handles an http GET.
                 Also, it prints the client address, the address string, command, path, parsed path, server version,
                 system version, and protocol version. Here is an example.

        CLIENT VALUES:
        client_address=('127.0.0.1', 56863) (1.0.0.127.in-addr.arpa)
        command=GET
        path=/
        real path=/
        query=
        request_version=HTTP/1.1

        SERVER VALUES:
        server_version=BaseHTTP/0.3
        sys_version=Python/2.7.15
        protocol_version=HTTP/1.0

        """
        parsed_path = urlparse.urlparse(self.path)
        message = '\n'.join([
            'CLIENT VALUES:',
            'client_address=%s (%s)' % (self.client_address,
                                        self.address_string()),
            'command=%s' % self.command,
            'path=%s' % self.path,
            'real path=%s' % parsed_path.path,
            'query=%s' % parsed_path.query,
            'request_version=%s' % self.request_version,
            '',
            'SERVER VALUES:',
            'server_version=%s' % self.server_version,
            'sys_version=%s' % self.sys_version,
            'protocol_version=%s' % self.protocol_version,
            '',
        ])
        self.send_response(200)
        self.end_headers()
        self.wfile.write(message)
        return

    def do_OPTIONS(self):
        """

        :return: It handles the OPTIONS request.
        """
        self.send_response(200, "ok")
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()

    def do_POST(self):
        """

        :return: It handles http POSTs. It calls functions to handle user requests. The four-step process is shown below.

        """
        # Step 1: Get the data


        content_len = int(self.headers.getheader('content-length'))
        post_body = self.rfile.read(content_len)

        # Step 2: Arrange the headers

        self.send_response(200, "ok")
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()

        # Step 3: Parse the data
        # print post_body
        data = json.loads(post_body)
        # print data
        # Step 4: Figure out what function to call
        self.real_time_inference(data["text"])
        
        return


if __name__ == '__main__':


    parser = argparse.ArgumentParser(description='Short sample app')

    parser.add_argument('-port', action="store", dest="port", type=int, default=30303, required=False)
    parser.add_argument('-address', action="store", dest="address", type=str, default='localhost', required=False)

    args = parser.parse_args()
    # addr = 'localhost'  # '192.168.125.204'
    # port = 30303
    addr = args.address
    port = args.port

    print('Starting server at http://'+ addr +':'+str(port))
    server = HTTPServer((addr, port), RequestHandler)
    try:
        server.serve_forever()
    except:
        mydb_file = open('../src/main/resources/minhashes.p','wb')
        pickle.dump(server.RequestHandlerClass.db, mydb_file)
        mydb_file.close()

        print('Closing server at http://'+ addr +':'+str(port))
