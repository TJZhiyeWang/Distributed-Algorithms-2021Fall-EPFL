#!/usr/bin/env python3

import os, atexit
import textwrap

class TC:
    def __init__(self, losses, interface="lo", needSudo=True, sudoPassword="dcl"):
        self.losses = losses
        self.interface = interface
        self.needSudo = needSudo
        self.sudoPassword = sudoPassword

        cmd1 = 'tc qdisc add dev {} root netem 2>/dev/null'.format(self.interface)
        cmd2 = 'tc qdisc change dev {} root netem delay {} {} distribution normal loss {} {} reorder {} {}'.format(self.interface, *self.losses['delay'], *self.losses['loss'], *self.losses['reordering'])

        if self.needSudo:
            os.system("echo {} | sudo -S {}".format(self.sudoPassword, cmd1))
            os.system("echo {} | sudo -S {}".format(self.sudoPassword, cmd2))
        else:
            os.system(cmd1)
            os.system(cmd2)

        atexit.register(self.cleanup)

    def __str__(self):
        ret = """\
        Interface: {}
          Distribution: Normal
          Delay: {} {}
          Loss: {} {}
          Reordering: {} {}""".format(
              self.interface,
              *self.losses['delay'],
              *self.losses['loss'],
              *self.losses['reordering'])

        return textwrap.dedent(ret)

    def cleanup(self):
        cmd = 'tc qdisc del dev {} root 2>/dev/null'.format(self.interface)
        if self.needSudo:
            os.system("echo '{}' | sudo -S {}".format(self.sudoPassword, cmd))
        else:
            os.system(cmd)

if __name__ == "__main__":
    # Network configuration using the tc command
    config = {
        'delay': ('200ms', '50ms'),
        'loss': ('10%', '25%'),
        'reordering': ('25%', '50%')
    }

    print("Do not have multiple instances of this script executing at the same time!\n")

    print("Adding network delay/loss/reordering")
    tc = TC(config)
    print(tc)
    print()

    input("Press `Enter` to exit (removes network delay/loss/reordering)")