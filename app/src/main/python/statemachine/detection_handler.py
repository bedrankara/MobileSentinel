from statemachine.detection_states import InitState
from parsers.qualcomm.pycrate.pycrate_asn1dir import RRCLTE
import string
from binascii import hexlify, unhexlify
import json

class Detection(object):

    def __init__(self):
        self.state = InitState()

    def check_packet(self, rrc_subtype, msg):

        if str(rrc_subtype) == "gsmtap_lte_rrc_types.DL_DCCH":

            sch = RRCLTE.EUTRA_RRC_Definitions.DL_DCCH_Message
            sch.from_uper(unhexlify(msg))
            json_string = sch.to_json()
            data = json.loads(json_string)
            if 'rrcConnectionReconfiguration' in data['message']['c1']:
                return data









    def on_event(self, rrc_subtype, msg):

        parsed_msg = self.check_packet(rrc_subtype, msg)
        if parsed_msg is not None:
            self.state = self.state.on_event(msg)



