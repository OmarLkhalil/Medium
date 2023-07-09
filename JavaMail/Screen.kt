package com.mobilebreakero.medium


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Composable
fun ReportProblemScreen() {

    val subjectState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val contentState = remember { mutableStateOf("") }
    val buttonText = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "You Can report any problem that you faced in the app.",
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
        )

        FormTextField(
            text = subjectState.value,
            placeholder = "Subject",
            onChange = { subjectState.value = it },
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            text = emailState.value,
            placeholder = "Your Email",
            onChange = { emailState.value = it },
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            text = contentState.value,
            placeholder = "problem description",
            onChange = { contentState.value = it },
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            modifier = Modifier.height(200.dp),
            maxLines = 8,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        buttonText.value = "Send"
        Button(
            modifier = Modifier
                .height(35.dp)
                .width(200.dp),
            onClick = {

                val subject = subjectState.value
                val email = emailState.value
                val content = contentState.value


                if (subject.isBlank() || email.isBlank() || content.isBlank()) {
                    Toast.makeText(context, "Fill all please", Toast.LENGTH_LONG).show()
                    return@Button
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(context, "Email isn't correct", Toast.LENGTH_LONG).show()
                    return@Button
                } else {
                    emailErrorState.value = ""
                }

                buttonText.value = "Sending..."

                sendEmail(email, subject, content, context, onSuccess = {
                    subjectState.value = ""
                    emailState.value = ""
                    contentState.value = ""
                    buttonText.value = "Send Again"
                })
            }
        ) {
            Text(text = buttonText.value)
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val pattern = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
    return email.matches(pattern)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    maxLines: Int,
    leadingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier
            .width(350.dp)
            .height(60.dp),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        maxLines = maxLines,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            disabledBorderColor = Color.Black,
            disabledTextColor = Color.Black
        ),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            )
        }
    )
}


fun sendEmail(
    from: String,
    subject: String,
    content: String,
    context: Context,
    onSuccess: () -> Unit
) {
    // Create a CoroutineScope using the IO dispatcher to perform IO operations to prevent UI blocking ANRs
    CoroutineScope(Dispatchers.IO).launch {
        // SMTP server details
        val host = "smtp.gmail.com"
        val port = 587
        val username = "omarkkhalil12@gmail.com"
        val password = "huybavqvhracezqf"

        // Email recipient
        val to = "omarkkhalil12@gmail.com"

        // Configure SMTP properties
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = host
        props["mail.smtp.port"] = port

        // Create a session with authentication
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            // Create a new MimeMessage
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            message.subject = subject
            message.setText(from + "\n" + content)

            // Send the message using the Transport class
            Transport.send(message)

            // Perform UI operations on the Main dispatcher
            CoroutineScope(Dispatchers.Main).launch {
                // Display a success toast message
                Toast.makeText(context, "Sent Successfully.", Toast.LENGTH_LONG).show()

                // Invoke the onSuccess callback function
                onSuccess.invoke()
            }
        } catch (e: MessagingException) {
            e.printStackTrace()

            // Perform UI operations on the Main dispatcher
            CoroutineScope(Dispatchers.Main).launch {
                // Display an error toast message
                Toast.makeText(context, "Problem exists: $e", Toast.LENGTH_LONG).show()
            }
        }
    }
}
