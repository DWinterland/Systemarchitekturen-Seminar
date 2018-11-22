function backupFile ( name , ext ) {
	var cmd = [];
	cmd . push ("cp");
	cmd . push ( name + "." + ext );
	cmd . push ("~/. localBackup /");

	exec ( cmd . join (" " ));

	var kind = ( ext === "jpg ") ? " pics " : " other ";
	console . log ( eval (" messages . backup_ " + kind ));
}
