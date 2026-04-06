import { Router } from "express";
import { supabase } from "../config/supabase.js";

export const tasksRouter = Router();

tasksRouter.get("/", async (req, res, next) => {
  try {
    const { userId, status, search, due_date } = req.query;

    if (!userId) {
      return res.status(400).json({ success: false, message: "userId is required" });
    }

    let query = supabase
      .from("tasks")
      .select("id, user_id, title, description, due_date, status")
      .eq("user_id", userId)
      .order("id", { ascending: false });

    if (status) query = query.eq("status", status);
    if (search) query = query.ilike("title", `%${search}%`);
    if (due_date) query = query.eq("due_date", due_date);

    const { data, error } = await query;
    if (error) throw error;

    return res.json({ success: true, tasks: data });
  } catch (err) {
    return next(err);
  }
});

tasksRouter.get("/completed", async (req, res, next) => {
  try {
    const { userId } = req.query;
    if (!userId) {
      return res.status(400).json({ success: false, message: "userId is required" });
    }

    const { data, error } = await supabase
      .from("tasks")
      .select("id, user_id, title, description, due_date, status")
      .eq("user_id", userId)
      .eq("status", "Completed")
      .order("id", { ascending: false });

    if (error) throw error;

    return res.json({ success: true, tasks: data });
  } catch (err) {
    return next(err);
  }
});

tasksRouter.post("/", async (req, res, next) => {
  try {
    const { userId, title, description = "", due_date } = req.body;

    if (!userId || !title || !due_date) {
      return res.status(400).json({ success: false, message: "userId, title and due_date are required" });
    }

    const { data, error } = await supabase
      .from("tasks")
      .insert({ user_id: userId, title, description, due_date, status: "Pending" })
      .select("id, user_id, title, description, due_date, status")
      .single();

    if (error) throw error;

    return res.status(201).json({ success: true, task: data });
  } catch (err) {
    return next(err);
  }
});

tasksRouter.patch("/:id/status", async (req, res, next) => {
  try {
    const { id } = req.params;
    const { status } = req.body;

    if (!status) {
      return res.status(400).json({ success: false, message: "status is required" });
    }

    const { data, error } = await supabase
      .from("tasks")
      .update({ status })
      .eq("id", id)
      .select("id, user_id, title, description, due_date, status")
      .maybeSingle();

    if (error) throw error;
    if (!data) {
      return res.status(404).json({ success: false, message: "Task not found" });
    }

    return res.json({ success: true, task: data });
  } catch (err) {
    return next(err);
  }
});

tasksRouter.delete("/:id", async (req, res, next) => {
  try {
    const { id } = req.params;

    const { data, error } = await supabase
      .from("tasks")
      .delete()
      .eq("id", id)
      .select("id")
      .maybeSingle();

    if (error) throw error;
    if (!data) {
      return res.status(404).json({ success: false, message: "Task not found" });
    }

    return res.json({ success: true, message: "Task deleted" });
  } catch (err) {
    return next(err);
  }
});
