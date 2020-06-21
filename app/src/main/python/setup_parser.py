#!/usr/bin/env python3
# coding: utf8


from android.os import Environment

import iodevices
import writers
import parsers
import subprocess
import string
import subprocess
import threading
import time
import os, sys, re, importlib
import argparse
import signal
import struct
import util
import faulthandler
import logging


GSMTAP_PORT = 4729
IP_OVER_UDP_PORT = 47290

def thread_logging(filename):

    print(subprocess.check_output(['su','-c', 'mkdir /data/media/0/logs/'+filename]))
    print(subprocess.check_output(['su','-c', 'chmod 777 /data/media/0/logs/'+filename]))
    #TODO: Queue dumps and parse correctly
    #TODO: Check if packets are being dropped once a new dump is started
    print(subprocess.check_output(['su','-c','diag_mdlog -s 90000 -f /data/media/0/logs/full_diag -o /data/media/0/logs/'+filename]))
    # p = subprocess.Popen(['su','-c','diag_mdlog -s 90000 -f /data/media/0/logs/full_diag -o /data/media/0/logs/'+filename], stdout=subprocess.PIPE)
    # out = p.stdout.read()
    # print(out)



def initiate_logging(filename):
    x = threading.Thread(target=thread_logging, args=(filename,))
    x.start()
    time.sleep(360)
    print(subprocess.check_output(['su','-c','diag_mdlog -k']))






def initiate_parsing(packet_list,dump_directory,dump_filename):

    # Hardcoded values
    my_parser = parsers.QualcommParser(packet_list)
    d = str(Environment.getExternalStorageDirectory());
    print(d)
    filepath = os.path.join(d, 'logs/' + dump_directory +'/'+dump_filename)
    print(filepath)
    io_device = iodevices.FileIO([str(filepath)])
    my_parser.set_io_device(io_device)
    writer = writers.PcapWriter(dump_directory, GSMTAP_PORT, IP_OVER_UDP_PORT)
    my_parser.set_writer(writer)
    my_parser.read_dump()