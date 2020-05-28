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
    print(subprocess.check_output(['su','-c','diag_mdlog -s 90000 -f /data/media/0/logs/Diag.cfg -o /data/media/0/logs/'+filename]))
    print("threadded logging started")
    cmd = "diag_mdlog -f /data/media/0/logs/Diag.cfg -o /data/media/0/logs"
    MyOut = subprocess.Popen(['su','-c','diag_mdlog -f /data/media/0/logs/Diag.cfg -o /data/media/0/logs'],
                             stdout=subprocess.PIPE,
                             stderr=subprocess.STDOUT)
    stdout,stderr = MyOut.communicate()
    print(stdout)
    print(stderr)
    print("thread logging ending")

def initiate_logging(filename):
    x = threading.Thread(target=thread_logging, args=(filename,))
    x.start()
    time.sleep(30)
    print("stopping")
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