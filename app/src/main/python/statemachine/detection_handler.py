from statemachine.detection_states import InitState


class Detection(object):

    def __init__(self):
        self.state = InitState()

    def on_event(self, rrc_subtype, msg):
        self.state = self.state.on_event(rrc_subtype, msg)
