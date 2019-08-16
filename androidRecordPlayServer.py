from flask import Flask, render_template
from flask_socketio import SocketIO, send, emit, join_room, leave_room
import eventlet
import eventlet.wsgi

# import soundcard as sc
import numpy as np
# import waveflask
from scipy.io.wavfile import read, write

app = Flask(__name__)
app.config['SECRET_KEY'] = 'testkey123'
socketio = SocketIO(app)
recorderConnected = False
deviceArr = []


@app.route('/')
def sessions():
    return render_template('session.html')

@socketio.on('join recorder') #recieves a 'join recorder' event (emit) from android device
def on_join_record(deviceName):
    room = 'recorder'
    join_room(room)
    recorderConnected = True
    emit('enable button', room='player')
    #from ActivateRecorder.java (80-81); emits 'join recorder' with an argument of deviceName 
    #deviceName = #what ever this is supposed to be
    
    print(deviceName + ' recorder registered')

@socketio.on('join player')
def on_join_player(deviceName):
    room = 'player'
    join_room(room)
    # send('entered the player room', room=room)
    if recorderConnected:
        emit('enable button', room='player')
    print(deviceName + ' registered as player')

@socketio.on('start collection')
def on_start_collection():
    print('data collection started')
    emit('start record', room='recorder')
    print('recording')
    emit('start play', room='player')
    print('playing')

@socketio.on('stop collection')
def on_stop_collection():
    emit('stop record', room='recorder')
    print('stop recording')

@socketio.on('hey waddup')
def on_waduup():
    print('i\'m fine bro')


@socketio.on('Send File')
def convert_file_to_wav(byteArr):
    #print('type: ')
    #print(type(byteArr[0]))
    #for stuff in byteArr:
    #    print(stuff)
    #print(byteArr[0])
    music = []
    for i in range(len(byteArr)):
        music.append(int.from_bytes(byteArr[i], 'big'))
    
    music_np = np.array(music)
    print(music_np)
    fs =  40000
    write('whatever.wav', fs, music_np)


if __name__ == '__main__':
    socketio.run(app, debug=True, host='0.0.0.0', port=8090)
#    app = socketio.Middleware(socketio, app)
#    eventlet.wsgi.server(eventlet.listen(('', 8090)), app)


