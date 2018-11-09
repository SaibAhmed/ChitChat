'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/notifications/{user_id}').onWrite((Change, context) => {

    console.log('Testing stuff', context.params.user_id)
});