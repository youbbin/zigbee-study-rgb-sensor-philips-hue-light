from digi.xbee.devices import XBeeDevice
import json
#TODO: Replace with the serial port where your local module is connected to.
PORT = "COM13"
#TODO: Replace with the baud rate of your local module.
BAUD_RATE = 9600


def main():
    print(" +-----------------------------------------+")
    print(" | XBee Python Library Receive Data Sample |")
    print(" +-----------------------------------------+\n")

    device = XBeeDevice(PORT, BAUD_RATE)

    try:
        device.open()

        def data_receive_callback(xbee_message):
            data = xbee_message.data.decode()
            print("From %s >> %s" % (xbee_message.remote_device.get_64bit_addr(),
                                     data))
            data_json = json.loads(data)
            print(data_json['illum'])
        device.add_data_received_callback(data_receive_callback)

        print("Waiting for data...\n")
        input()

    finally:
        if device is not None and device.is_open():
            device.close()


if __name__ == '__main__':
    main()