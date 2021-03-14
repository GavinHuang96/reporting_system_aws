import base64
import json
from googleapiclient.discovery import build
from google.oauth2.credentials import Credentials
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

SCOPES = ['https://mail.google.com/']

def sendEmail(to, subject, body):
    creds = Credentials.from_authorized_user_file("token.json", SCOPES)
    service = build("gmail", "v1", credentials=creds)
    mimeMessage = MIMEMultipart()
    mimeMessage["to"] = to
    mimeMessage["subject"] = subject
    mimeMessage.attach(MIMEText(body, "plain"))
    raw_string = base64.urlsafe_b64encode(mimeMessage.as_bytes()).decode()
    try:
        service.users().messages().send(userId="me", body={"raw": raw_string}).execute()
        print ("email sent to " + to)
    except:
        print ("error sending mail to " + to)

def lambda_handler(event, context):
    for record in event["Records"]:
        payload = record["body"]
        payload = json.loads(payload)

        if payload["token"] == "12345":
            to = payload["to"]
            subject = payload["subject"]
            body = payload["body"]
            sendEmail(to, subject, body)
    return "done"
