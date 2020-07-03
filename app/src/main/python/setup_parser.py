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
import os.path
from os import path


GSMTAP_PORT = 4729
IP_OVER_UDP_PORT = 47290


def start_logging(filename):
    # Check if log folder exists and has permissions
    try:
        print(subprocess.check_output(['su','-c', 'mkdir /data/media/0/logs']))
        print(subprocess.check_output(['su','-c', 'chmod 777 /data/media/0/logs']))
    except Exception as e:
        print(e)
    # Initiate mdlog and pass config files
    print(subprocess.check_output(['su','-c', 'mkdir /data/media/0/logs/'+filename]))
    print(subprocess.check_output(['su','-c', 'chmod 777 /data/media/0/logs/'+filename]))
    subprocess.Popen(['su','-c','diag_mdlog -s 90000 -f /data/media/0/logs/RRCDIAG.cfg -o /data/media/0/logs/'+filename])
    print("Mdlog is running")

def stop_logging():
    #subprocess.Popen(['su','-c','diag_mdlog -k'])
    print(subprocess.check_output(['su','-c','diag_mdlog -k']))
    print("Mdlog is stopped")

def initiate_parsing(packet_list,dump_directory,dump_filename, detection_view=None):

    # Setup dump parser modules
    my_parser = parsers.QualcommParser(packet_list, detection_view)
    d = str(Environment.getExternalStorageDirectory());
    print(d)
    filepath = os.path.join(d, 'logs/' + dump_directory +'/'+dump_filename)
    print(filepath)
    io_device = iodevices.FileIO([str(filepath)])
    my_parser.set_io_device(io_device)
    writer = writers.PcapWriter(dump_directory, GSMTAP_PORT, IP_OVER_UDP_PORT)
    my_parser.set_writer(writer)
    my_parser.read_dump()

