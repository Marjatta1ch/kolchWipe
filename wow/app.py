import connexion
import base64
import numpy as np
import cv2
import os
import tensorflow as tf
from tensorflow.keras import datasets, layers, models

trained_model_location = 'saved_model/alexnet-4char-with-upper-letters'
model = tf.keras.models.load_model(trained_model_location)

def format_y(y):
    return ''.join(map(lambda x: chr(int(x)), y))

def solve(image):
    nparr = np.fromstring(base64.b64decode(image), np.uint8)
    im = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    im = np.array(im) / 255.0
    im = np.array(im)
    y_pred = model.predict(np.array([im]))
    y_pred = tf.math.argmax(y_pred, axis=-1)
    return format_y(y_pred[0])


app = connexion.App(__name__, specification_dir='./')
app.add_api('swagger.yaml')
app.run(port=os.environ.get('PORT'))
