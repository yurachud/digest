<#-- @ftlvariable name="" type="lv.latcraft.digest.DigestView" -->
<html>
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
	$(function () {
		$(function () {
			var self = this
			$("#sendButton").click(function () {
				var queryParams = {to: $("#to").val(), skipEventId: $("#skipEventId").val()};
				$("#sendButton").prop('disabled', true);
				$("#sendButton").attr('value', 'Please wait')
				$.post("recipients",
						queryParams,
						function (numberOfEmails) {
							if (confirm("Are you sure you want to send Emails to " + numberOfEmails + " participants")) {
								$('#digestForm').submit();
							}
							$("#sendButton").prop('disabled', false);
							$("#sendButton").attr('value', 'Send')
						});
			});
		});
	});
</script>
<body>
<h1>Digest application:</h1>


<div style="width: 400px; float: left; margin-left: 10px">
	<form id="digestForm" method="POST" action="/send">
		<div style="float: left; width: 1000px">
			<table>
				<tr>
					<td>Path to local password repo:</td>
					<td>${pathToPasswordRepo}</td>
				</tr>
				<tr>
					<td>From:</td>
					<td><input name="from" id="from" value="hello@latcraft.lv"/></td>
					<td>(Usually default value is used)</td>
				</tr>
				<tr>
					<td>To:</td>
					<td><input name="to" id="to"></td>
					<td>(If empty, everyone from eventbrite email list will receive an email)</td>
				</tr>
				<tr>
					<td>Subject:</td>
					<td><input name="subject" id="subject" required></td>
					<td>(Email subject should be defined)</td>
				</tr>
				<tr>
					<td>Skip event id:</td>
					<td><input name="skipEventId" id="skipEventId"/></td>
					<td>(if specified, does not collect members of the given event)</td>
				</tr>
			</table>
		</div>
		<p>Body in markdown: </p>
		<textarea id="markdown" name="markdown" rows="20" cols="80" required></textarea>
		<input id="sendButton" type="button" value="Send"/>
	</form>
</div>
</body>
</html>