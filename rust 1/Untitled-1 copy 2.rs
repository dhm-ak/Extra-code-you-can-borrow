#[macro_use]
extern crate rocket;

use rocket::fs::{NamedFile, relative};
use rocket::http::{RawStr, Status};
use rocket::request::{FromRequest, Outcome};
use rocket::{Request, State};
use rocket_contrib::databases::diesel;

mod models;
use models::{User, load_all_users};

#[get("/hello")]
fn hello() -> &'static str {
    "Hello, world!"
}

fn auth(req: &Request) -> Outcome<(), Status> {
    // Check authentication here...
    if /* authenticated */ {
        Outcome::Success(())
    } else {
        Outcome::Failure((Status::Unauthorized, Status::Unauthorized.reason))
    }
}

#[get("/private")]
fn private() -> &'static str {
    "This is a private message"
}

#[database("my_db")]
struct MyDbConn(diesel::PgConnection);

#[get("/users")]
fn list_users(conn: MyDbConn) -> String {
    let users = load_all_users(&conn.0);
    let user_list = users.iter()
        .map(|u| format!("{} - {}", u.id, u.name))
        .collect::<Vec<_>>()
        .join("\n");
    format!("Users:\n{}", user_list)
}

#[derive(serde::Serialize)]
struct TemplateContext {
    title: String,
    message: String,
}

#[get("/hello/<name>")]
fn hello_template(name: &RawStr) -> rocket_contrib::templates::Template {
    let context = TemplateContext {
        title: format!("Hello, {}!", name),
        message: "Welcome to my Rust web app!".to_owned(),
    };
    rocket_contrib::templates::Template::render("hello", &context)
}

#[get("/static/<file..>")]
fn static_files(file: relative::PathBuf) -> Option<NamedFile> {
    NamedFile::open(relative!("static", file)).ok()
}

#[rocket::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    rocket::build()
        .mount("/", routes![hello, private, list_users, hello_template, static_files])
        .manage(auth)
        .attach(MyDbConn::fairing())
        .attach(rocket_contrib::templates::Template::fairing())
        .launch()
        .await?;
    Ok(())
}
