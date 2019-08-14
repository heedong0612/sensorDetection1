from flask import Flask, render_template
from flask_socketio import SocketIO, send, emit, join_room, leave_room
import eventlet
import eventlet.wsgi

# import soundcard as sc
# import numpy as np
# import waveflask
# from scipy.io.wavfile import read

app = Flask(__name__)
app.config['SECRET_KEY'] = 'testkey123'
socketio = SocketIO(app)

@app.route('/')
def sessions():
    return render_template('session.html')

@socketio.on('join recorder') #recieves a 'join recorder' event (emit) from android device
def on_join_record(deviceName):
    room = 'recorder'
    join_room(room)
    
    #from ActivateRecorder.java (80-81); emits 'join recorder' with an argument of deviceName 
    #deviceName = #what ever this is supposed to be
    
    print(deviceName + ' recorder registered')

@socketio.on('join player')
def on_join_player():
    room = 'player'
    join_room(room)
    # send('entered the player room', room=room)
    print('player registered')

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
#print('stop recording')

@socketio.on('hey waddup')
def on_waduup():
    print('i\'m fine bro')

if __name__ == '__main__':
    socketio.run(app, debug=True, host='0.0.0.0', port=8090)
#    app = socketio.Middleware(socketio, app)
#    eventlet.wsgi.server(eventlet.listen(('', 8090)), app)


