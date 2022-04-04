CREATE TABLE IF NOT EXISTS ao_msg
(
    msg_id      INTEGER
                PRIMARY KEY AUTOINCREMENT,
    msg         TEXT NOT NULL,
    msg_type    VARCHAR NOT NULL,
    player      VARCHAR NOT NULL,
    created_at   BIGINT NOT NULL
);