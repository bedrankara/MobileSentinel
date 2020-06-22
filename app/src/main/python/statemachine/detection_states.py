from statemachine.state import State
from parsers.qualcomm.pycrate.pycrate_asn1dir import RRCLTE
from binascii import hexlify, unhexlify
import json
import string


class InitState(State):

    def on_event(self, msg):

        try:
            if 'radioResourceConfigDedicated' in \
                    msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions'][
                        'c1']['rrcConnectionReconfiguration-r8']:
                if 'drb-ToReleaseList' in \
                        msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions'][
                            'c1']['rrcConnectionReconfiguration-r8']['radioResourceConfigDedicated']:
                   # print("drb-ToReleaseList present")
                    for iter in \
                    msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions']['c1'][
                        'rrcConnectionReconfiguration-r8']['radioResourceConfigDedicated'][
                        'drb-ToReleaseList']:

                        #print("First drb release received")
                        print(iter)
                    return InitState()

        except KeyError as e:
            print(e)
        return InitState()


class DrbIdState(State):

    def on_event(self, msg):
        try:
            if 'radioResourceConfigDedicated' in \
                    msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions'][
                        'c1']['rrcConnectionReconfiguration-r8']:
                if 'drb-ToReleaseList' in \
                        msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions'][
                            'c1']['rrcConnectionReconfiguration-r8']:
                    print("drb-ToReleaseList present")
                    for iter in \
                            msg['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions']['c1'][
                                'rrcConnectionReconfiguration-r8']['radioResourceConfigDedicated'][
                                'drb-ToReleaseList']:

                        print("DrbIDState")
                        print(iter)
                    return DrbIdState()

        except KeyError as e:
            print(e)
        return DrbIdState()
