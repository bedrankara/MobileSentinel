#!/usr/bin/env python3
# coding: utf8

import iodevices
import writers
import parsers

import subprocess
from android.os import Environment

import string


import os, sys, re, importlib
import argparse
import signal
import struct
import util
import faulthandler
import logging


GSMTAP_PORT = 4729
IP_OVER_UDP_PORT = 47290

def initiate_parsing(packet_list):

    # Hardcoded values
    my_parser = parsers.QualcommParser(packet_list)
    d = str(Environment.getExternalStorageDirectory());
    filepath = os.path.join(d, 'premiumsimVolte.qmdl')
    print(filepath)
    io_device = iodevices.FileIO([str(filepath)])
    my_parser.set_io_device(io_device)
    writer = writers.PcapWriter('premiumsimVolte', GSMTAP_PORT, IP_OVER_UDP_PORT)
    my_parser.set_writer(writer)
    my_parser.read_dump()