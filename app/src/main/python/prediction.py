import numpy as np
from os.path import dirname, join
import pickle
import tensorflow as tf

CLASSES_LIST = ['ขอบคุณ', 'ทำงาน', 'ธุระ', 'รัก', 'สบายดี', 'สวัสดี', 'หิว', 'เข้าใจ', 'เสียใจ', 'ไม่สบาย']

THRESHOLD = 0.8

scaler_file = open(join(dirname(__file__), 'scaler_angles.pkl'), 'rb')
scaler_angle = pickle.load(scaler_file)

model_file = join(dirname(__file__), 'thai_sign_language_model.h5')
model = tf.keras.models.load_model(model_file, compile = False)

def translation(frame_list, empty_param):
    features_model = np.expand_dims(np.asarray(frame_list), axis=0)
    features_model[0:,0:,0:50] = scaler_angle.transform(features_model[0:,0:,0:50].reshape(features_model.shape[0], -1)).reshape(features_model[0:,0:,0:50].shape)

    res = model.predict(features_model)[0]

    if res[np.argmax(res)] >= THRESHOLD:
        return CLASSES_LIST[np.argmax(res)]
    else:
        return "ไม่ทราบท่าทาง"