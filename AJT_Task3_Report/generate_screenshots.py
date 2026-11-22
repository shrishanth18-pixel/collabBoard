from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path('/Users/shrishanthm/CollabBoard')
OUT = ROOT / 'AJT_Task3_Report' / 'Code_Screenshots'
OUT.mkdir(parents=True, exist_ok=True)

FONT_CANDIDATES = ['/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf', '/System/Library/Fonts/Supplemental/Menlo.ttc']
font_path = next((p for p in FONT_CANDIDATES if Path(p).exists()), None)
font = ImageFont.truetype(font_path, 21) if font_path else ImageFont.load_default()
small = ImageFont.truetype(font_path, 17) if font_path else font

snippets = {
    'servlet_implementation.png': [
        ('collabApp/src/main/java/Workspace/DeleteTaskServlet.java', 13, 40),
    ],
    'jsp_implementation.png': [
        ('collabApp/src/main/webapp/WEB-INF/jspFiles/login.jsp', 1, 2),
        ('collabApp/src/main/webapp/WEB-INF/jspFiles/login.jsp', 181, 217),
    ],
    'jstl_el.png': [
        ('collabApp/src/main/webapp/WEB-INF/jspFiles/dashboard.jsp', 1, 3),
        ('collabApp/src/main/webapp/WEB-INF/jspFiles/dashboard.jsp', 448, 467),
    ],
    'spring_dependency_injection.png': [
        ('collabApp/src/main/java/config/AppConfig.java', 12, 36),
        ('collabApp/src/main/java/service/BoardService.java', 8, 18),
    ],
    'hibernate_entities.png': [
        ('collabApp/src/main/java/model/BoardEntity.java', 11, 46),
        ('collabApp/src/main/java/repository/BoardHibernateRepository.java', 12, 24),
    ],
    'crud_operations.png': [
        ('collabApp/src/main/java/db/DB.java', 32, 73),
        ('collabApp/src/main/java/db/Update.java', 7, 39),
        ('collabApp/src/main/java/db/Delete.java', 6, 25),
    ],
    'session_management.png': [
        ('collabApp/src/main/java/Workspace/login.java', 60, 91),
        ('collabApp/src/main/java/Workspace/AuthFilter.java', 15, 30),
        ('collabApp/src/main/java/Workspace/logout.java', 11, 27),
    ],
    'form_validation.png': [
        ('collabApp/src/main/webapp/WEB-INF/jspFiles/login.jsp', 193, 217),
        ('collabApp/src/main/java/Workspace/register.java', 24, 60),
    ],
    'swing_implementation.png': [
        ('collabApp/src/main/java/desktop/CollabBoardSwingApp.java', 41, 117),
        ('collabApp/src/main/java/desktop/CollabBoardSwingApp.java', 158, 200),
    ],
}

for filename, ranges in snippets.items():
    blocks = []
    for rel, start, end in ranges:
        path = ROOT / rel
        lines = path.read_text().splitlines()
        blocks.append((rel, start, end, lines[start-1:end]))

    max_width = 1500
    line_height = 30
    padding = 34
    header_height = 72
    separator_height = 34
    height = header_height + sum(len(lines) * line_height + separator_height for _,_,_,lines in blocks) + padding

    image = Image.new('RGB', (max_width, height), '#f8fafc')
    draw = ImageDraw.Draw(image)

    y = padding
    for index, (rel, start, end, lines) in enumerate(blocks):
        if index:
            y += 18
        draw.rounded_rectangle((padding, y, max_width-padding, y+48), radius=8, fill='#1e293b')
        label = f'{rel}  |  Lines {start}-{end}'
        draw.text((padding+16, y+10), label, font=small, fill='#f8fafc')
        y += 58

        for n, text in zip(range(start, end+1), lines):
            number = f'{n:4d}'
            draw.text((padding+8, y+2), number, font=small, fill='#94a3b8')
            draw.text((padding+78, y), text if text else ' ', font=font, fill='#0f172a')
            y += line_height
        y += 12

    image.save(OUT / filename, quality=95)
    print(filename)
