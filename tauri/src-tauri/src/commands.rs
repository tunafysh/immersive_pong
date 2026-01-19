use tauri::Manager; // for emit

#[derive(serde::Serialize)]
struct GameState {
    ball_x: f32,
    ball_y: f32,
    left_paddle: f32,
    right_paddle: f32,
}

#[tauri::command]
fn get_ai_move(ball_y: f32, paddle_y: f32) -> f32 {
    // Example AI logic: follow the ball
    (ball_y - paddle_y).signum() * 5.0
}

fn send_game_state(app: &tauri::AppHandle, state: &GameState) {
    app.emit_all("game:update", state).unwrap();
}
