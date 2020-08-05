from binascii import hexlify, unhexlify
from parsers.qualcomm.pycrate.pycrate_asn1dir import RRCLTE

import json
import string


class DetectionHandler:

    def __init__(self):
        self.initial_drb_id = 0
        self.drb_id = 0
        self.call_flow = []

    def filter_packet(self, rrc_subtype, msg):
        if str(rrc_subtype) == "gsmtap_lte_rrc_types.DL_DCCH":

            sch = RRCLTE.EUTRA_RRC_Definitions.DL_DCCH_Message
            sch.from_uper(unhexlify(msg))
            json_string = sch.to_json()
            data = json.loads(json_string)
            if 'rrcConnectionReconfiguration' in data['message']['c1']:
                return data

    def revolte_check(self, rrc_subtype, msg):
        parsed_msg = self.filter_packet(rrc_subtype, msg)
        if parsed_msg is not None:

            try:
                if 'radioResourceConfigDedicated' in \
                        parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                            'criticalExtensions'][
                            'c1']['rrcConnectionReconfiguration-r8']:
                    if 'drb-ToReleaseList' in \
                            parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                                'criticalExtensions'][
                                'c1']['rrcConnectionReconfiguration-r8'][
                                'radioResourceConfigDedicated']:
                        for iter in \
                                parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                                    'criticalExtensions']['c1'][
                                    'rrcConnectionReconfiguration-r8'][
                                    'radioResourceConfigDedicated'][
                                    'drb-ToReleaseList']:

                            if len(self.call_flow) != 0:

                                if iter in self.call_flow and 'securityConfigHO' not in self.call_flow:

                                    print("VULNERABLE ENB!!!!!")
                                    return True

                            self.call_flow.append(iter)
                            #self.call_flow.append(4)
                            print(self.call_flow)


            except KeyError as e:
                print(e)

            try:
                if 'securityConfigHO' in \
                        parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                            'criticalExtensions'][
                            'c1']['rrcConnectionReconfiguration-r8']:

                    if 'handoverType' in \
                            parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                                'criticalExtensions'][
                                'c1']['rrcConnectionReconfiguration-r8']['securityConfigHO']:

                        for iter in \
                                parsed_msg['message']['c1']['rrcConnectionReconfiguration'][
                                    'criticalExtensions']['c1'][
                                    'rrcConnectionReconfiguration-r8']['securityConfigHO'][
                                    'handoverType']:
                            print("HandoverType")
                            self.call_flow.append('securityConfigHO')
                            print(iter)



            except KeyError as e:
                print(e)
