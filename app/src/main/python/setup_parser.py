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


def initiate_parsing():

    # Hardcoded values
    my_parser = parsers.QualcommParser()
    d = str(Environment.getExternalStorageDirectory());
    filepath = os.path.join(d, 'test.qmdl')
    print(filepath)
    io_device = iodevices.FileIO([str(filepath)])
    my_parser.set_io_device(io_device)
    my_parser.set_writer(writer)
    my_parser.read_dump()