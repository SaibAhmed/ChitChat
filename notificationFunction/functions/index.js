'use strict'

const functions = require('firebase-functions');
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotification = functions.database.ref("/notifications/{user_id}").onWrite((change, context) => {
  const user_id = context.params.user_id;
  console.log("we have a notification to send to", user_id);


});