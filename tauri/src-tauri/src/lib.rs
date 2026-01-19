#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_barcode_scanner::init())
        .plugin(tauri_plugin_websocket::init())
        setup(|app| {
        let app_handle = app.handle();
        if cfg!(debug_assertions) {
                app_handle().plugin(
                    tauri_plugin_log::Builder::default()
                        .level(log::LevelFilter::Info)
                        .build(),
                )?;
            }
        std::thread::spawn(move || {
            let mut game = Game::new();
            let frame_duration = std::time::Duration::from_millis(16);

            loop {
                // AI moves
                let left_move = get_ai_move(game.ball.y, game.left_paddle.y);
                game.left_paddle.y += left_move;

                // Example right paddle could be human via frontend input

                // Update ball & collisions
                game.update_ball();

                // Send game state to frontend
                let state = GameState {
                    ball_x: game.ball.x,
                    ball_y: game.ball.y,
                    left_paddle: game.left_paddle.y,
                    right_paddle: game.right_paddle.y,
                };
                send_game_state(&app_handle, &state);

                std::thread::sleep(frame_duration);
            }
        });
        Ok(())
    })        
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
