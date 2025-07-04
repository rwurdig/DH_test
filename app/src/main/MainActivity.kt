setContent {
    var showInput by remember { mutableStateOf(true) }
    if (showInput) {
        BirthDataInputScreen(
            viewModel = viewModel(),
            onSubmitSuccess = {   // add parameter in composable signature
                showInput = false
            }
        )
    } else {
        // Later you’ll pass real chart data here
        BodyGraphPainter(Modifier.fillMaxSize())
    }
}
