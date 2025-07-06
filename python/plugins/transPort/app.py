from flask import Flask, render_template, redirect, url_for, request, flash, session, jsonify
from extensions import db
from werkzeug.security import generate_password_hash, check_password_hash
import os
import re

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your_secret_key_here'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

with app.app_context():
    from models import User, Job, Driver, Agent, Billing, Discount

    if not os.path.exists('database.db'):
        db.create_all()


    # Create default admin user if not exists
    def create_default_admin():
        admin = User.query.filter_by(username='admin').first()
        if not admin:
            admin = User(username='admin', email='admin@example.com')
            admin.set_password('admin')
            db.session.add(admin)
            db.session.commit()


    create_default_admin()


@app.route('/')
def index():
    if 'user_id' in session:
        return redirect(url_for('dashboard'))
    return redirect(url_for('login'))


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        user = User.query.filter_by(username=username).first()
        if user and user.check_password(password):
            session['user_id'] = user.id
            return redirect(url_for('dashboard'))
        else:
            flash('Invalid username or password')
    return render_template('login.html')


@app.route('/logout')
def logout():
    session.pop('user_id', None)
    return redirect(url_for('login'))


@app.route('/reset_password', methods=['GET', 'POST'])
def reset_password():
    if request.method == 'POST':
        email = request.form['email']
        user = User.query.filter_by(email=email).first()
        if user:
            new_password = request.form['new_password']
            user.set_password(new_password)
            db.session.commit()
            flash('Password reset successful. Please login.')
            return redirect(url_for('login'))
        else:
            flash('Email not found.')
    return render_template('reset_password.html')


@app.route('/dashboard')
def dashboard():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    return render_template('dashboard.html')


# JOBS CRUD
@app.route('/jobs', methods=['GET', 'POST'])
def jobs():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    search_query = request.args.get('search', '')
    if search_query:
        jobs = Job.query.filter(
            (Job.customer_name.ilike(f'%{search_query}%')) |
            (Job.customer_email.ilike(f'%{search_query}%')) |
            (Job.customer_mobile.ilike(f'%{search_query}%')) |
            (Job.customer_reference.ilike(f'%{search_query}%')) |
            (Job.passenger_name.ilike(f'%{search_query}%')) |
            (Job.passenger_email.ilike(f'%{search_query}%')) |
            (Job.passenger_mobile.ilike(f'%{search_query}%')) |
            (Job.type_of_service.ilike(f'%{search_query}%')) |
            (Job.pickup_date.ilike(f'%{search_query}%')) |
            (Job.pickup_time.ilike(f'%{search_query}%')) |
            (Job.pickup_location.ilike(f'%{search_query}%')) |
            (Job.dropoff_location.ilike(f'%{search_query}%')) |
            (Job.vehicle_type.ilike(f'%{search_query}%')) |
            (Job.vehicle_number.ilike(f'%{search_query}%')) |
            (Job.driver_contact.ilike(f'%{search_query}%')) |
            (Job.payment_mode.ilike(f'%{search_query}%')) |
            (Job.payment_status.ilike(f'%{search_query}%')) |
            (Job.order_status.ilike(f'%{search_query}%')) |
            (Job.message.ilike(f'%{search_query}%')) |
            (Job.remarks.ilike(f'%{search_query}%')) |
            (Job.reference.ilike(f'%{search_query}%')) |
            (Job.status.ilike(f'%{search_query}%'))
        ).all()
    else:
        jobs = Job.query.all()
    return render_template('jobs.html', jobs=jobs, search_query=search_query)


@app.route('/jobs/add', methods=['GET', 'POST'])
def add_job():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        job = Job(
            customer_name=request.form.get('customer_name'),
            customer_email=request.form.get('customer_email'),
            customer_mobile=request.form.get('customer_mobile'),
            customer_reference=request.form.get('customer_reference'),
            passenger_name=request.form.get('passenger_name'),
            passenger_email=request.form.get('passenger_email'),
            passenger_mobile=request.form.get('passenger_mobile'),
            type_of_service=request.form.get('type_of_service'),
            pickup_date=request.form.get('pickup_date'),
            pickup_time=request.form.get('pickup_time'),
            pickup_location=request.form.get('pickup_location'),
            dropoff_location=request.form.get('dropoff_location'),
            vehicle_type=request.form.get('vehicle_type'),
            vehicle_number=request.form.get('vehicle_number'),
            driver_contact=request.form.get('driver_contact'),
            payment_mode=request.form.get('payment_mode'),
            payment_status=request.form.get('payment_status'),
            order_status=request.form.get('order_status'),
            message=request.form.get('message'),
            remarks=request.form.get('remarks'),
            has_additional_stop=bool(request.form.get('has_additional_stop')),
            has_request=bool(request.form.get('has_request')),
            reference=request.form.get('reference'),
            status=request.form.get('status'),
            date=request.form.get('pickup_date')
        )
        db.session.add(job)
        db.session.commit()
        return redirect(url_for('jobs'))
    return render_template('job_form.html', action='Add', job=None)


@app.route('/jobs/edit/<int:job_id>', methods=['GET', 'POST'])
def edit_job(job_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    job = Job.query.get_or_404(job_id)
    if request.method == 'POST':
        job.customer_name = request.form.get('customer_name')
        job.customer_email = request.form.get('customer_email')
        job.customer_mobile = request.form.get('customer_mobile')
        job.customer_reference = request.form.get('customer_reference')
        job.passenger_name = request.form.get('passenger_name')
        job.passenger_email = request.form.get('passenger_email')
        job.passenger_mobile = request.form.get('passenger_mobile')
        job.type_of_service = request.form.get('type_of_service')
        job.pickup_date = request.form.get('pickup_date')
        job.pickup_time = request.form.get('pickup_time')
        job.pickup_location = request.form.get('pickup_location')
        job.dropoff_location = request.form.get('dropoff_location')
        job.vehicle_type = request.form.get('vehicle_type')
        job.vehicle_number = request.form.get('vehicle_number')
        job.driver_contact = request.form.get('driver_contact')
        job.payment_mode = request.form.get('payment_mode')
        job.payment_status = request.form.get('payment_status')
        job.order_status = request.form.get('order_status')
        job.message = request.form.get('message')
        job.remarks = request.form.get('remarks')
        job.has_additional_stop = bool(request.form.get('has_additional_stop'))
        job.has_request = bool(request.form.get('has_request'))
        job.reference = request.form.get('reference')
        job.status = request.form.get('status')
        job.date = request.form.get('pickup_date')
        db.session.commit()
        return redirect(url_for('jobs'))
    return render_template('job_form.html', action='Edit', job=job)


@app.route('/jobs/delete/<int:job_id>', methods=['POST'])
def delete_job(job_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    job = Job.query.get_or_404(job_id)
    db.session.delete(job)
    db.session.commit()
    return redirect(url_for('jobs'))


@app.route('/jobs/smart_add', methods=['GET', 'POST'])
def smart_add_job():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    parsed_data = None
    if request.method == 'POST':
        message = request.form.get('message')
        parsed_data = parse_job_message(message)
        return render_template('job_form.html', action='Add', job=parsed_data, smart_add=True, pasted_message=message)
    return render_template('smart_add.html')


def parse_job_message(message):
    # Simple regex-based parser for demo; can be improved for real-world messages
    data = {}
    # Example: "Customer: John Doe, Email: john@example.com, Mobile: 1234567890, Service: Airport Transfer, Date: 2024-07-01, Time: 10:00, Pickup: Hotel, Drop: Airport, Vehicle: Sedan, Driver: Mike, Payment: Paid, Status: Confirmed"
    patterns = {
        'customer_name': r'Customer[:\-]?\s*([\w\s]+)',
        'customer_email': r'Email[:\-]?\s*([\w\.-]+@[\w\.-]+)',
        'customer_mobile': r'Mobile[:\-]?\s*(\d+)',
        'type_of_service': r'Service[:\-]?\s*([\w\s]+)',
        'pickup_date': r'Date[:\-]?\s*([\d\-/]+)',
        'pickup_time': r'Time[:\-]?\s*([\d:apmAPM\s]+)',
        'pickup_location': r'Pickup[:\-]?\s*([\w\s]+)',
        'dropoff_location': r'Drop[:\-]?\s*([\w\s]+)',
        'vehicle_type': r'Vehicle[:\-]?\s*([\w\s]+)',
        'driver_contact': r'Driver[:\-]?\s*([\w\s]+)',
        'payment_status': r'Payment[:\-]?\s*([\w\s]+)',
        'order_status': r'Status[:\-]?\s*([\w\s]+)',
    }
    for field, pattern in patterns.items():
        match = re.search(pattern, message, re.IGNORECASE)
        if match:
            data[field] = match.group(1).strip()
    return data


# DRIVERS CRUD
@app.route('/drivers')
def drivers():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    drivers = Driver.query.all()
    return render_template('drivers.html', drivers=drivers)


@app.route('/drivers/add', methods=['GET', 'POST'])
def add_driver():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        name = request.form['name']
        phone = request.form['phone']
        driver = Driver(name=name, phone=phone)
        db.session.add(driver)
        db.session.commit()
        return redirect(url_for('drivers'))
    return render_template('driver_form.html', action='Add')


@app.route('/drivers/edit/<int:driver_id>', methods=['GET', 'POST'])
def edit_driver(driver_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    driver = Driver.query.get_or_404(driver_id)
    if request.method == 'POST':
        driver.name = request.form['name']
        driver.phone = request.form['phone']
        db.session.commit()
        return redirect(url_for('drivers'))
    return render_template('driver_form.html', action='Edit', driver=driver)


@app.route('/drivers/delete/<int:driver_id>', methods=['POST'])
def delete_driver(driver_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    driver = Driver.query.get_or_404(driver_id)
    db.session.delete(driver)
    db.session.commit()
    return redirect(url_for('drivers'))


# AGENTS CRUD
@app.route('/agents')
def agents():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    agents = Agent.query.all()
    return render_template('agents.html', agents=agents)


@app.route('/agents/add', methods=['GET', 'POST'])
def add_agent():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        name = request.form['name']
        agent = Agent(name=name)
        db.session.add(agent)
        db.session.commit()
        return redirect(url_for('agents'))
    return render_template('agent_form.html', action='Add')


@app.route('/agents/edit/<int:agent_id>', methods=['GET', 'POST'])
def edit_agent(agent_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    agent = Agent.query.get_or_404(agent_id)
    if request.method == 'POST':
        agent.name = request.form['name']
        db.session.commit()
        return redirect(url_for('agents'))
    return render_template('agent_form.html', action='Edit', agent=agent)


@app.route('/agents/delete/<int:agent_id>', methods=['POST'])
def delete_agent(agent_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    agent = Agent.query.get_or_404(agent_id)
    db.session.delete(agent)
    db.session.commit()
    return redirect(url_for('agents'))


# BILLING CRUD
@app.route('/billing')
def billing():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    billings = Billing.query.all()
    return render_template('billing.html', billings=billings)


@app.route('/billing/add', methods=['GET', 'POST'])
def add_billing():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        job_id = request.form['job_id']
        amount = request.form['amount']
        discount_id = request.form['discount_id']
        billing = Billing(job_id=job_id, amount=amount, discount_id=discount_id)
        db.session.add(billing)
        db.session.commit()
        return redirect(url_for('billing'))
    return render_template('billing_form.html', action='Add')


@app.route('/billing/edit/<int:billing_id>', methods=['GET', 'POST'])
def edit_billing(billing_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    billing = Billing.query.get_or_404(billing_id)
    if request.method == 'POST':
        billing.job_id = request.form['job_id']
        billing.amount = request.form['amount']
        billing.discount_id = request.form['discount_id']
        db.session.commit()
        return redirect(url_for('billing'))
    return render_template('billing_form.html', action='Edit', billing=billing)


@app.route('/billing/delete/<int:billing_id>', methods=['POST'])
def delete_billing(billing_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    billing = Billing.query.get_or_404(billing_id)
    db.session.delete(billing)
    db.session.commit()
    return redirect(url_for('billing'))


# DISCOUNTS CRUD
@app.route('/discounts')
def discounts():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    discounts = Discount.query.all()
    return render_template('discounts.html', discounts=discounts)


@app.route('/discounts/add', methods=['GET', 'POST'])
def add_discount():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        code = request.form['code']
        percent = request.form['percent']
        discount = Discount(code=code, percent=percent)
        db.session.add(discount)
        db.session.commit()
        return redirect(url_for('discounts'))
    return render_template('discount_form.html', action='Add')


@app.route('/discounts/edit/<int:discount_id>', methods=['GET', 'POST'])
def edit_discount(discount_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    discount = Discount.query.get_or_404(discount_id)
    if request.method == 'POST':
        discount.code = request.form['code']
        discount.percent = request.form['percent']
        db.session.commit()
        return redirect(url_for('discounts'))
    return render_template('discount_form.html', action='Edit', discount=discount)


@app.route('/discounts/delete/<int:discount_id>', methods=['POST'])
def delete_discount(discount_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))
    discount = Discount.query.get_or_404(discount_id)
    db.session.delete(discount)
    db.session.commit()
    return redirect(url_for('discounts'))


if __name__ == '__main__':
    app.run(debug=True)
