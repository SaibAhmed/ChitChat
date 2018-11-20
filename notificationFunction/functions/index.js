'use strict'
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotification = functions.database
  .ref('/notifications/{user_id}')
  .onWrite((change, context) => {
    const user_id = context.params.user_id;
    const notification = event.params.notification;

    console.log("the user id is :", user_id);
  });
