'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Notif/{Reciever}')
.onWrite((data, context) => {
    const Reciever = context.params.Reciever;

    if(!data.after.val()) return null;

    const token = admin.database().ref(`/Member/${Reciever}/device_token`).once('value');
    return token.then(result =>{
        const token_id = result.val();

        const payload = {
            notification:{
                title: "New Update Request",
				body: `you got new new Update, Please Check.`,
				icon: "default"
            }
        };

        return admin.messaging().sendToDevice(token_id, payload)
        .then(response =>{
            console.log("Sent Notification :D response "+ Reciever+" "+token_id);
        });
    });
});