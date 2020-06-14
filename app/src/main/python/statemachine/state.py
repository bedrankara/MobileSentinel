class State(object):

    def __init__(self):
        print("Current state:", str(self))

    def on_event(self, event):
        pass

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return self.__class__.__name__
