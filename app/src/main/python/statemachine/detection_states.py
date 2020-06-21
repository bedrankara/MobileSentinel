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
        if str(rrc_subtype) != "gsmtap_lte_rrc_types.DL_DCCH":
            return DrbIdState()

        sch = RRCLTE.EUTRA_RRC_Definitions.DL_DCCH_Message
        sch.from_uper(unhexlify(msg))
        json_string = sch.to_json()
        data = json.loads(json_string)
        try:
            if 'radioResourceConfigDedicated' in \
                    data['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions'][
                        'c1']['rrcConnectionReconfiguration-r8']:
                for iter in \
                data['message']['c1']['rrcConnectionReconfiguration']['criticalExtensions']['c1'][
                    'rrcConnectionReconfiguration-r8']['radioResourceConfigDedicated'][
                    'drb-ToReleaseList']:
                    print("DRB ID STATE First drb release received")
                    print(iter)
                    return DrbIdState()

        except Exception:
            print(Exception)
        return DrbIdState()
