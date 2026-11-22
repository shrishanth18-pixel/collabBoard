-- Complete CollabBoard PostgreSQL initialization
-- Run once against an empty database, or with a compatible existing database.

DO $$ BEGIN
    CREATE TYPE priority_order AS ENUM ('Low', 'Medium', 'High');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE project_status AS ENUM ('Planning', 'Active', 'On Hold', 'Completed');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE task_status AS ENUM ('Todo', 'In Progress', 'Done');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('OWNER', 'EDITOR', 'VIEWER');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS board (
    id SERIAL PRIMARY KEY,
    projectname VARCHAR(50) NOT NULL,
    status project_status NOT NULL DEFAULT 'Planning',
    priority priority_order NOT NULL DEFAULT 'Medium',
    deadline TIMESTAMP WITH TIME ZONE,
    description VARCHAR(1000),
    created_by INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS board_member (
    users_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    board_id INTEGER NOT NULL REFERENCES board(id) ON DELETE CASCADE,
    role user_role NOT NULL DEFAULT 'EDITOR',
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (users_id, board_id)
);

CREATE TABLE IF NOT EXISTS task (
    taskid SERIAL PRIMARY KEY,
    taskname VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    taskstatus task_status NOT NULL DEFAULT 'Todo',
    deadline TIMESTAMP WITH TIME ZONE,
    priority priority_order NOT NULL DEFAULT 'Medium',
    board_id INTEGER NOT NULL REFERENCES board(id) ON DELETE CASCADE,
    assigned_to VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friendships (
    friendship_id SERIAL PRIMARY KEY,
    requester_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    addressee_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(requester_id, addressee_id),
    CHECK(requester_id <> addressee_id),
    CHECK(status IN ('PENDING','ACCEPTED','DECLINED'))
);

CREATE TABLE IF NOT EXISTS board_invitations (
    invitation_id SERIAL PRIMARY KEY,
    board_id INTEGER NOT NULL REFERENCES board(id) ON DELETE CASCADE,
    inviter_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    invitee_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role user_role NOT NULL DEFAULT 'EDITOR',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(board_id, invitee_id),
    CHECK(invitee_id <> inviter_id),
    CHECK(status IN ('PENDING','ACCEPTED','DECLINED'))
);

CREATE INDEX IF NOT EXISTS idx_board_member_user ON board_member(users_id);
CREATE INDEX IF NOT EXISTS idx_board_member_board ON board_member(board_id);
CREATE INDEX IF NOT EXISTS idx_task_board ON task(board_id);
CREATE INDEX IF NOT EXISTS idx_friendships_requester ON friendships(requester_id);
CREATE INDEX IF NOT EXISTS idx_friendships_addressee ON friendships(addressee_id);
CREATE INDEX IF NOT EXISTS idx_board_invitations_invitee ON board_invitations(invitee_id);
