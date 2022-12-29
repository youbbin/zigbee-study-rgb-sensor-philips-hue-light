from flask import Flask, request, Response    # Flask 관련 라이브러리
from digi.xbee.devices import XBeeDevice    # Zigbee 관련 라이브러리
import json
app = Flask(__name__)

PORT = "COM13"  # 포트번호
BAUD_RATE = 9600  # baudrate


r="default"
g="default"
b="default"
cct="default"
illum="default"

def xbeeReceive(): # 데이터 수신 함수
    print(">>>>>>>>>>>XBee 수신 시작>>>>>>>>>>>")
    device = XBeeDevice(PORT, BAUD_RATE)
    try:
        device.open()

        while True:
            xbee_message = device.read_data()  # 데이터 읽기
            if xbee_message is not None:
                data_receive = xbee_message.data.decode()
                print("From %s >> %s" % (xbee_message.remote_device.get_64bit_addr(), data_receive))
                global data_json
                data_json = json.loads(data_receive)
                # global r
                # r= str(data_json['cct'])
       # def data_receive_callback(xbee_message): # 데이터 수신 콜백 함수
        #     data = xbee_message.data.decode()
        #     print("From %s >> %s" % (xbee_message.remote_device.get_64bit_addr(),
        #                              data))
        #
        #     data_json = json.loads(data)
        #     r=data_json['cct']

        # device.add_data_received_callback(data_receive_callback)

        print("데이터 기다리는 중...\n")


    finally:
        if device is not None and device.is_open():
            device.close()

@app.route('/')
def startXbee():
    Response(xbeeReceive())
    return 'OK'

@app.route('/rgbSensorValue', methods = ['GET'])
def rgbsensor_get():
    return data_json


if __name__ == '__main__':
    app.run(debug=True, host="210.102.142.15")


