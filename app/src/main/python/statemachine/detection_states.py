from statemachine.state import State
from parsers.qualcomm.pycrate.pycrate_asn1dir import RRCLTE
from binascii import hexlify, unhexlify
import json
import string


class InitState(State):

    def on_event(self, rrc_subtype, msg):

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

                        print("First drb release received")
                        print(iter)
                    return DrbIdState()

        except Exception:
            print(Exception)
        return InitState()


class DrbIdState(State):

    def on_event(self, rrc_subtype, msg):
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

                        print("First drb release received")
                        print(iter)
                    return DrbIdState()

        except Exception:
            print(Exception)
        return DrbIdState()
